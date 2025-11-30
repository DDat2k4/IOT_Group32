package org.example.web.service;

import lombok.RequiredArgsConstructor;
import org.example.web.data.entity.Device;
import org.example.web.data.entity.Sensor;
import org.example.web.data.request.SensorRequest;
import org.example.web.data.response.SensorResponse;
import org.example.web.repository.DeviceRepository;
import org.example.web.repository.SensorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepository sensorRepository;
    private final DeviceRepository deviceRepository;

    public SensorResponse createSensor(Long deviceId, SensorRequest request) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        Sensor s = new Sensor();
        s.setDevice(device);
        s.setSensorType(request.getSensorType());
        s.setName(request.getName());
        s.setUnit(request.getUnit());
        s.setMinValue(request.getMinValue());
        s.setMaxValue(request.getMaxValue());
        s.setStatus("ACTIVE");

        sensorRepository.save(s);
        return toResponse(s);
    }

    public List<SensorResponse> getByDevice(Long deviceId) {
        return sensorRepository.findByDeviceId(deviceId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public SensorResponse update(Long id, SensorRequest request) {
        Sensor s = sensorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sensor not found"));

        s.setName(request.getName());
        s.setUnit(request.getUnit());
        s.setMinValue(request.getMinValue());
        s.setMaxValue(request.getMaxValue());

        sensorRepository.save(s);
        return toResponse(s);
    }

    public void delete(Long id) {
        sensorRepository.deleteById(id);
    }

    private SensorResponse toResponse(Sensor s) {
        SensorResponse r = new SensorResponse();
        r.setId(s.getId());
        r.setSensorType(s.getSensorType());
        r.setName(s.getName());
        r.setUnit(s.getUnit());
        r.setMinValue(s.getMinValue());
        r.setMaxValue(s.getMaxValue());
        r.setStatus(s.getStatus());
        return r;
    }

    // Lấy sensor theo device + type
    public Sensor findByDeviceAndType(Long deviceId, String sensorType) {
        return sensorRepository.findByDeviceIdAndSensorType(deviceId, sensorType).orElse(null);
    }
}
