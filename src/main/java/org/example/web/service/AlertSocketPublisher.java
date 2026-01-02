package org.example.web.service;

import lombok.RequiredArgsConstructor;
import org.example.web.data.pojo.AlertSocketDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlertSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void pushAlert(AlertSocketDTO alert) {
        messagingTemplate.convertAndSend(
                "/topic/alerts",
                alert
        );
    }
}

