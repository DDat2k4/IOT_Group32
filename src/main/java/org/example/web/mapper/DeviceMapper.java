package org.example.web.mapper;

import org.example.web.data.entity.Device;
import org.example.web.data.entity.Sensor;
import org.example.web.data.response.DeviceResponse;
import org.example.web.data.response.SensorResponse;

import java.util.List;
import java.util.stream.Collectors;

public class DeviceMapper {

    public static DeviceResponse toResponse(Device device) {
        DeviceResponse res = new DeviceResponse();
        res.setId(device.getId());
        res.setDeviceCode(device.getDeviceCode());
        res.setName(device.getName());
        res.setLocation(device.getLocation());
        res.setStatus(device.getStatus());

        if (device.getSensors() != null) {
            List<SensorResponse> sensors = device.getSensors().stream()
                    .map(DeviceMapper::toSensorResponse)
                    .collect(Collectors.toList());
            res.setSensors(sensors);
        }

        return res;
    }

    public static SensorResponse toSensorResponse(Sensor sensor) {
        SensorResponse res = new SensorResponse();
        res.setId(sensor.getId());
        res.setSensorType(sensor.getSensorType());
        res.setName(sensor.getName());
        res.setUnit(sensor.getUnit());
        res.setMinValue(sensor.getMinValue());
        res.setMaxValue(sensor.getMaxValue());
        res.setStatus(sensor.getStatus());
        return res;
    }

    public static List<DeviceResponse> toResponseList(List<Device> devices) {
        return devices.stream().map(DeviceMapper::toResponse).collect(Collectors.toList());
    }
}
