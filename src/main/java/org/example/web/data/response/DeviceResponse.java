package org.example.web.data.response;

import lombok.Data;

@Data
public class DeviceResponse {
    private Long id;
    private String deviceCode;
    private String name;
    private String location;
    private String status;
}

