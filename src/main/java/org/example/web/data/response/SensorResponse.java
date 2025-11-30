package org.example.web.data.response;

import lombok.Data;

@Data
public class SensorResponse {
    private Long id;
    private String sensorType;
    private String name;
    private String unit;
    private Float minValue;
    private Float maxValue;
    private String status;
}

