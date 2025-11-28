package org.example.web.data.pojo;

import lombok.Data;

@Data
public class MqttMessageDTO {
    private String topic;
    private String payload;
}

