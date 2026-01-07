package org.example.web.mapper;

import org.example.web.data.entity.Alert;
import org.example.web.data.response.AlertResponse;
import org.springframework.stereotype.Component;

@Component
public class AlertMapper {

    public AlertResponse toResponse(Alert a) {
        AlertResponse res = new AlertResponse();

        res.setId(a.getId());

        res.setDeviceCode(
                a.getDevice() != null ? a.getDevice().getDeviceCode() : null
        );
        res.setName(
                a.getDevice() != null ? a.getDevice().getName() : null
        );

        res.setSensorType(
                a.getSensor() != null ? a.getSensor().getSensorType() : null
        );
        res.setSensorUnit(
                a.getSensor() != null ? a.getSensor().getUnit() : null
        );

        res.setAlertType(a.getAlertType());
        res.setLevel(a.getAlertLevel());

        res.setValue(
                a.getValue() != null ? a.getValue().doubleValue() : null
        );
        res.setThreshold(
                a.getThreshold() != null ? a.getThreshold().doubleValue() : null
        );

        res.setCreatedAt(
                a.getCreatedAt() != null ? a.getCreatedAt().toString() : null
        );

        return res;
    }
}
