package org.example.web.mapper;

import org.example.web.data.entity.Alert;
import org.example.web.data.response.AlertResponse;
import org.springframework.stereotype.Component;

@Component
public class AlertMapper {

    public AlertResponse toResponse(Alert a) {
        AlertResponse res = new AlertResponse();
        res.setId(a.getId());

        // Lấy deviceId từ object Device
        res.setDeviceId(a.getDevice() != null ? a.getDevice().getId() : null);

        // Lấy sensorId từ object Sensor
        res.setSensorId(a.getSensor() != null ? a.getSensor().getId() : null);

        res.setMessage(a.getAlertType());
        res.setLevel(a.getAlertLevel());
        res.setCreatedAt(a.getCreatedAt() != null ? a.getCreatedAt().toString() : null);
        return res;
    }
}


