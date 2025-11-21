package org.example.web.data;

import lombok.Data;

@Data
public class MqttMessageDTO {
    private String topic;
    private String payload;
}

