package org.example.web.service;

import org.example.web.data.entity.Device;
import org.example.web.repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Device findByDeviceCode(String deviceCode) {
        return deviceRepository.findByDeviceCode(deviceCode);
    }

    public Device save(Device device) {
        return deviceRepository.save(device);
    }

    public Optional<Device> findById(Long id) {
        return deviceRepository.findById(id);
    }
}

