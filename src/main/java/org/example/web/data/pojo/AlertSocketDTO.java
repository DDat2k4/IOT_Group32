package org.example.web.data.pojo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AlertSocketDTO {
    private String deviceCode;
    private String sensorType;
    private String alertType;
    private String alertLevel;
    private Float threshold;
    private String deviceName;
    private String sensorName;
    private Float value;
    private LocalDateTime createdAt;
}