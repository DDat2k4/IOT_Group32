package org.example.web.mapper;

import org.example.web.data.entity.Device;
import org.example.web.data.entity.Sensor;
import org.example.web.data.response.DeviceResponse;
import org.example.web.data.response.SensorResponse;

import java.util.stream.Collectors;

public class DeviceMapper {

    public static DeviceResponse toResponse(Device device) {
        DeviceResponse dto = new DeviceResponse();
        dto.setId(device.getId());
        dto.setDeviceCode(device.getDeviceCode());
        dto.setName(device.getName());
        dto.setLocation(device.getLocation());
        dto.setStatus(device.getStatus());

        if (device.getSensors() != null) {
            dto.setSensors(
                    device.getSensors().stream()
                            .map(DeviceMapper::toSensorResponse)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    private static SensorResponse toSensorResponse(Sensor sensor) {
        SensorResponse dto = new SensorResponse();
        dto.setId(sensor.getId());
        dto.setSensorType(sensor.getSensorType());
        dto.setName(sensor.getName());
        dto.setUnit(sensor.getUnit());
        dto.setMinValue(sensor.getMinValue());
        dto.setMaxValue(sensor.getMaxValue());
        dto.setStatus(sensor.getStatus());
        return dto;
    }
}
