package org.example.web.config;

import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.example.web.data.entity.Alert;
import org.example.web.data.entity.Device;
import org.example.web.data.entity.MessageLog;
import org.example.web.data.entity.Sensor;
import org.example.web.service.AlertService;
import org.example.web.service.DeviceService;
import org.example.web.service.MessageLogService;
import org.example.web.service.SensorService;
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

@Component
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

    public MqttSSLConfig(AlertService alertService, DeviceService deviceService,
                         SensorService sensorService, MessageLogService messageLogService) {
        this.alertService = alertService;
        this.deviceService = deviceService;
        this.sensorService = sensorService;
        this.messageLogService = messageLogService;
    }

    @PostConstruct
    public void init() throws Exception {

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

        // MQTT Options
        MqttConnectOptions options = new MqttConnectOptions();
        options.setSocketFactory(sslContext.getSocketFactory());
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);

        client = new MqttClient(mqttServer, clientId, new MemoryPersistence());
        client.connect(options);

        log.info("MQTT TLS Connected to EMQX!");

        // Subscribe topic
        client.subscribe(topic, (t, msg) -> {
            String payload = new String(msg.getPayload());
            log.info("Received message from topic {}: {}", t, payload);

            // Lưu message log
            saveMessageLog(t, payload);

            try {
                // Parse deviceCode từ topic, ví dụ topic = iot/fire/ESP32-001
                String[] parts = t.split("/");
                String deviceCode = parts.length > 2 ? parts[2] : null;

                if (deviceCode != null) {
                    Device device = deviceService.findByDeviceCode(deviceCode);
                    if (device != null) {
                        processPayload(device, t, payload);
                    } else {
                        log.warn("Device not found for code {}", deviceCode);
                    }
                } else {
                    log.warn("Cannot parse deviceCode from topic: {}", t);
                }

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
            log.warn("Invalid payload, missing sensorType or value: {}", payload);
            return;
        }

        // Tìm sensor theo device và type
        Sensor sensor = sensorService.findByDeviceAndType(device.getId(), sensorType);

        // Xác định alertType và ngưỡng
        Float threshold = sensor != null ? sensor.getMaxValue() : null;
        boolean isWarning = threshold != null && value > threshold;

        String alertType = sensorType;
        if (sensorType.equalsIgnoreCase("MQ2")) alertType = "SMOKE";
        else if (sensorType.equalsIgnoreCase("FLAME")) alertType = "FIRE";
        else if (sensorType.equalsIgnoreCase("DHT11") && value > threshold) alertType = "TEMP_HIGH";

        Alert alert = Alert.builder()
                .device(device)
                .sensor(sensor)
                .alertType(alertType)
                .value(value)
                .threshold(threshold)
                .topic(topic)
                .payload(payload)
                .createdAt(LocalDateTime.now())
                .isWarning(isWarning)
                .build();

        logAlertAsync(alert);
    }

    @Async
    public void logAlertAsync(Alert alert) {
        alertService.logAlert(alert);
        log.info("Alert saved for device {} sensor {}",
                alert.getDevice().getDeviceCode(),
                alert.getSensor() != null ? alert.getSensor().getSensorType() : "N/A");
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

    // Publish message
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
