package org.example.web.service;

import lombok.RequiredArgsConstructor;
import org.example.web.data.entity.Alert;
import org.example.web.data.pojo.AlertSocketDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlertSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void pushAlert(Alert alert) {
        // Lấy userId từ alert
        Long userId = alert.getUser().getId();

        // Tạo DTO gửi cho frontend
        AlertSocketDTO dto = AlertSocketDTO.builder()
                .alertType(alert.getAlertType())
                .alertLevel(alert.getAlertLevel())
                .value(alert.getValue())
                .threshold(alert.getThreshold())
                .deviceName(alert.getDevice().getName())
                .sensorName(alert.getSensor() != null ? alert.getSensor().getName() : null)
                .createdAt(alert.getCreatedAt())
                .build();

        // Gửi riêng cho user
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/alerts",
                dto
        );
    }
}


