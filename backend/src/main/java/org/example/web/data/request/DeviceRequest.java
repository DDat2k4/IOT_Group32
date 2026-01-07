package org.example.web.data.request;

import lombok.Data;

@Data
public class DeviceRequest {
    private String deviceCode;
    private String name;
    private String location;
}

