package org.example.web.service;

import org.example.web.data.entity.Device;
import org.example.web.repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Optional<Device> findById(Long id) {
        return deviceRepository.findById(id);
    }

    public List<Device> findAll() {
        return deviceRepository.findAll();
    }

    public Device save(Device device) {
        return deviceRepository.save(device);
    }

    public Device updateDevice(Long id, Device updated) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        device.setName(updated.getName());
        device.setLocation(updated.getLocation());
        device.setStatus(updated.getStatus());

        return deviceRepository.save(device);
    }

    public void deleteDevice(Long id) {
        if (!deviceRepository.existsById(id)) {
            throw new RuntimeException("Device not found");
        }
        deviceRepository.deleteById(id);
    }

    public Device updateStatus(String deviceCode, String status) {
        Device device = deviceRepository.findByDeviceCode(deviceCode);

        if (device == null) {
            throw new RuntimeException("Device not found");
        }

        device.setStatus(status);
        return deviceRepository.save(device);
    }

    public boolean existsByDeviceCode(String deviceCode) {
        return deviceRepository.existsByDeviceCode(deviceCode);
    }
}


