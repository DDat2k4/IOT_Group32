package org.example.web.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.example.web.data.entity.*;
import org.example.web.data.pojo.AlertSocketDTO;
import org.example.web.service.*;
import org.example.web.service.mail.MailService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MqttSSLConfig {

    private static final Logger log = LoggerFactory.getLogger(MqttSSLConfig.class);

    @Value("${mqtt.server}")
    private String mqttServer;
    @Value("${mqtt.username}")
    private String username;
    @Value("${mqtt.password}")
    private String password;
    @Value("${mqtt.client-id}")
    private String clientId;
    @Value("${mqtt.topic}")
    private String topic;
    @Value("${mqtt.ca-file}")
    private Resource caFile;

    private MqttClient client;

    private final AlertService alertService;
    private final DeviceService deviceService;
    private final SensorService sensorService;
    private final MessageLogService messageLogService;
    private final MailService mailService;
    private final AlertSocketPublisher alertSocketPublisher;

    @PostConstruct
    public void init() throws Exception {
        if (topic != null) {
            topic = topic.trim();
        }
        log.info("Subscribing to topic '{}'", topic);
        // Load CA file
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        try (InputStream caInput = caFile.getInputStream()) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            java.security.cert.Certificate ca = cf.generateCertificate(caInput);
            ks.setCertificateEntry("caCert", ca);
        }
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, tmf.getTrustManagers(), null);

        // MQTT connect
        MqttConnectOptions options = new MqttConnectOptions();
        options.setSocketFactory(sslContext.getSocketFactory());
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);

        client = new MqttClient(mqttServer, clientId, new MemoryPersistence());
        client.connect(options);
        // Subscribe topic
        log.info("Subscribing to topic '{}'", topic);
        log.info("Topic raw length={}, value=[{}]", topic.length(), topic);
        client.subscribe(topic, (t, msg) -> {
            String payload = new String(msg.getPayload());
            log.info("Received message from topic {}: {}", t, payload);

            saveMessageLog(t, payload);

            try {
                String[] parts = t.split("/");
                String deviceCode = parts.length > 2 ? parts[2] : null;
                if (deviceCode == null) {
                    log.warn("Cannot parse deviceCode from topic: {}", t);
                    return;
                }

                Device device = deviceService.findByDeviceCode(deviceCode);
                if (device == null) {
                    log.warn("Device not found for deviceCode {}", deviceCode);
                    return;
                }

                processPayload(device, t, payload);

            } catch (Exception e) {
                log.error("Error processing MQTT message: {}", e.getMessage(), e);
            }
        });
    }

    private void processPayload(Device device, String topic, String payload) {
        JSONObject obj = new JSONObject(payload);
        String sensorType = obj.optString("sensorType");
        Float value = obj.has("value") ? obj.getFloat("value") : null;
        if (sensorType == null || value == null) {
            log.warn("Invalid payload: {}", payload);
            return;
        }

        // Chuẩn hóa sensorType
        sensorType = sensorType.toUpperCase();

        Sensor sensor = sensorService.findByDeviceAndType(device.getId(), sensorType);
        if (sensor == null || sensor.getMaxValue() == null) {
            log.warn("Sensor or threshold not found for {}", sensorType);
            return;
        }

        Float maxValue = sensor.getMaxValue();
        Float mediumThreshold = maxValue * 0.8f;

        // Xác định mức cảnh báo
        String alertLevel = "NORMAL";
        boolean isWarning = false;

        if (value >= maxValue) {
            alertLevel = "HIGH";
            isWarning = true;
        } else if (value > mediumThreshold) {
            alertLevel = "MEDIUM";
            isWarning = true;
        }

        // Xác định loại cảnh báo
        String alertType;
        switch (sensorType) {
            case "CO":
                alertType = "Khí CO";
                break;
            case "MQ2":
                alertType = "Khí Gas";
                break;
            case "FLAME":
                alertType = "Lửa";
                break;
            case "TEMP":
                alertType = "Nhiệt độ";
                break;
            default:
                alertType = "UNKNOWN";
        }

        // Gửi alert cho tất cả user của device
        List<UserAccount> users = deviceService.getUsersOfDevice(device.getId());
        for (UserAccount user : users) {

            Alert alert = Alert.builder()
                    .device(device)
                    .sensor(sensor)
                    .user(user)
                    .alertType(alertType)
                    .alertLevel(alertLevel)
                    .value(value)
                    .threshold(maxValue)
                    .topic(topic)
                    .payload(payload)
                    .createdAt(LocalDateTime.now())
                    .isWarning(isWarning)
                    .build();
            logAlertAsync(alert);

            // Chỉ gửi email khi MEDIUM hoặc HIGH
            if (isWarning) {
                mailService.sendAlertEmail(user.getEmail(), alert);
                alertSocketPublisher.pushAlert(alert);
            }
        }
    }

    @Async
    public void logAlertAsync(Alert alert) {
        alertService.logAlert(alert);
        log.info("Alert saved for device {} sensor {} user {}",
                alert.getDevice().getDeviceCode(),
                alert.getSensor() != null ? alert.getSensor().getSensorType() : "N/A",
                alert.getUser() != null ? alert.getUser().getUsername() : "N/A");
    }

    @Async
    public void saveMessageLog(String topic, String payload) {
        MessageLog logEntry = MessageLog.builder()
                .topic(topic)
                .payload(payload)
                .receivedAt(LocalDateTime.now())
                .build();
        messageLogService.save(logEntry);
    }

    public void publish(String topic, String payload) throws MqttException {
        if (client != null && client.isConnected()) {
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(1);
            client.publish(topic, message);
            log.info("Published message to topic {}: {}", topic, payload);
        } else {
            throw new MqttException(new Throwable("MQTT client is not connected"));
        }
    }
}