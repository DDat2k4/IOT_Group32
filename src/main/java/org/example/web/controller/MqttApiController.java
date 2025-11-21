package org.example.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.web.config.MqttSSLConfig;
import org.example.web.data.MqttMessageDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mqtt")
public class MqttApiController {

    private final MqttSSLConfig mqtt;

    @PostMapping("/send")
    public String sendMessage(@RequestBody MqttMessageDTO dto) throws Exception {
        mqtt.publish(dto.getTopic(), dto.getPayload());
        return "Message sent to topic: " + dto.getTopic();
    }
}

