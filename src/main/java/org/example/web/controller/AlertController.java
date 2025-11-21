package org.example.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.web.config.MqttSSLConfig;
import org.example.web.data.MqttMessageDTO;
import org.example.web.data.entity.Alert;
import org.example.web.service.AlertService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;
    private final MqttSSLConfig mqtt;

    // Gửi command tới sensor
    @PostMapping("/send-command")
    public String sendCommand(@RequestBody MqttMessageDTO dto) throws Exception {
        mqtt.publish(dto.getTopic(), dto.getPayload());
        return "Command sent to topic: " + dto.getTopic();
    }

}
