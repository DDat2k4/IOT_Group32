package org.example.web.service;

import lombok.RequiredArgsConstructor;
import org.example.web.data.entity.Device;
import org.example.web.data.entity.Sensor;
import org.example.web.data.request.SensorRequest;
import org.example.web.data.response.SensorResponse;
import org.example.web.mapper.SensorMapper;
import org.example.web.repository.DeviceRepository;
import org.example.web.repository.SensorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepository sensorRepository;
    private final DeviceRepository deviceRepository;

    public SensorResponse createSensor(Long deviceId, SensorRequest req) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        if (sensorRepository.existsByDeviceIdAndSensorType(deviceId, req.getSensorType())) {
            throw new RuntimeException("Sensor type already exists in this device");
        }

        Sensor s = Sensor.builder()
                .device(device)
                .sensorType(req.getSensorType())
                .name(req.getName())
                .unit(req.getUnit())
                .minValue(req.getMinValue())
                .maxValue(req.getMaxValue())
                .status(req.getStatus() != null ? req.getStatus() : "ACTIVE")
                .build();

        return SensorMapper.toResponse(sensorRepository.save(s));
    }

    public List<SensorResponse> getByDevice(Long deviceId) {
        return sensorRepository.findByDeviceId(deviceId)
                .stream()
                .map(SensorMapper::toResponse)
                .toList();
    }

    public SensorResponse update(Long id, SensorRequest req) {
        Sensor s = sensorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sensor not found"));

        // Update fields
        s.setSensorType(req.getSensorType());
        s.setName(req.getName());
        s.setUnit(req.getUnit());
        s.setMinValue(req.getMinValue());
        s.setMaxValue(req.getMaxValue());
        s.setStatus(req.getStatus());

        return SensorMapper.toResponse(sensorRepository.save(s));
    }

    public void delete(Long id) {
        if (!sensorRepository.existsById(id)) {
            throw new RuntimeException("Sensor not found");
        }
        sensorRepository.deleteById(id);
    }

    public Sensor findByDeviceAndType(Long deviceId, String sensorType) {
        return sensorRepository.findByDeviceIdAndSensorType(deviceId, sensorType)
                .orElse(null);
    }

    public Sensor getOneByDeviceCode(String deviceCode) {
        return sensorRepository.findTopByDeviceDeviceCodeOrderByIdAsc(deviceCode);
    }

    @Transactional
    public void syncStatusWithDevice(Long deviceId, String status) {
        sensorRepository.updateStatusByDeviceId(deviceId, status);
    }
}