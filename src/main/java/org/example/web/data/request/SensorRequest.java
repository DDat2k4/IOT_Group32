package org.example.web.data.request;

import lombok.Data;

@Data
public class SensorRequest {
    private String sensorType;
    private String name;
    private String unit;
    private Float minValue;
    private Float maxValue;
    private String status;
}

