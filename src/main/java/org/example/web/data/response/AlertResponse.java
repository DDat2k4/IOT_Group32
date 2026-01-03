package org.example.web.data.response;

import lombok.Data;

@Data
public class AlertResponse {
    private Long id;
    private String deviceCode;
    private String name;
    private String sensorType;
    private String sensorUnit;
    private String AlertType;
    private String level;
    private String createdAt;
    private Double value;
    private Double threshold;
}


