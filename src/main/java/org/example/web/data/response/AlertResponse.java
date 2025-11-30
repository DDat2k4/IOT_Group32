package org.example.web.data.response;

import lombok.Data;

@Data
public class AlertResponse {
    private Long id;
    private Long deviceId;
    private Long sensorId;
    private String message;
    private String level;
    private String createdAt;
}

