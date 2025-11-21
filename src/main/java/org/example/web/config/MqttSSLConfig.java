package org.example.web.config;

import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.example.web.data.entity.Alert;
import org.example.web.data.entity.Device;
import org.example.web.service.AlertService;
import org.example.web.service.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
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

    public MqttSSLConfig(AlertService alertService, DeviceService deviceService) {
        this.alertService = alertService;
        this.deviceService = deviceService;
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

            // Parse deviceCode từ topic, ví dụ topic = iot/fire/ESP32-001
            String[] parts = t.split("/");
            String deviceCode = parts.length > 2 ? parts[2] : null;

            if (deviceCode != null) {
                Device device = deviceService.findByDeviceCode(deviceCode);
                if (device != null) {
                    Alert alert = Alert.builder()
                            .device(device)
                            .topic(t)
                            .payload(payload)
                            .createdAt(LocalDateTime.now())
                            .isWarning(true)
                            .build();

                    alertService.saveAlert(alert);
                    log.info("Alert saved for device {}", deviceCode);
                } else {
                    log.warn("Device not found for code {}", deviceCode);
                }
            } else {
                log.warn("Cannot parse deviceCode from topic: {}", t);
            }
        });
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
