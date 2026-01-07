package org.example.web.mapper;

import org.example.web.data.entity.Sensor;
import org.example.web.data.request.SensorRequest;
import org.example.web.data.response.SensorResponse;

public class SensorMapper {

    public static SensorResponse toResponse(Sensor sensor) {
        if (sensor == null) return null;

        SensorResponse res = new SensorResponse();
        res.setId(sensor.getId());
        res.setSensorType(sensor.getSensorType());
        res.setName(sensor.getName());
        res.setUnit(sensor.getUnit());
        res.setMinValue(sensor.getMinValue());
        res.setMaxValue(sensor.getMaxValue());
        res.setStatus(sensor.getStatus());
        if (sensor.getDevice() != null) {
            res.setDeviceId(sensor.getDevice().getId());
        }

        return res;
    }

    public static void updateSensor(Sensor sensor, SensorRequest req) {
        sensor.setSensorType(req.getSensorType());
        sensor.setName(req.getName());
        sensor.setUnit(req.getUnit());
        sensor.setMinValue(req.getMinValue());
        sensor.setMaxValue(req.getMaxValue());
        sensor.setStatus(req.getStatus());
    }
}
