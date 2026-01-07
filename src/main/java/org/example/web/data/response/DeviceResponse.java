package org.example.web.data.response;

import lombok.Data;

import java.util.List;

@Data
public class DeviceResponse {
    private Long id;
    private String deviceCode;
    private String name;
    private String location;
    private String status;
    private List<SensorResponse> sensors;
    private List<UserResponse> users;
}
