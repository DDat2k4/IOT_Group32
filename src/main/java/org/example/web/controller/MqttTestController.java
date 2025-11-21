package org.example.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.web.config.MqttSSLConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MqttTestController {

    private final MqttSSLConfig mqtt;

    @GetMapping("/test")
    public String test() throws Exception {
        mqtt.publish("iot/fire/test", "Hello from Spring Boot via SSL");
        return "Sent!";
    }
}
