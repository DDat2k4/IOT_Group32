package org.example.web.service;

import lombok.RequiredArgsConstructor;
import org.example.web.data.entity.Device;
import org.example.web.data.entity.UserAccount;
import org.example.web.repository.DeviceRepository;
import org.example.web.repository.UserAccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserAccountRepository userAccountRepository;

    // Save device và gán user hiện tại
    public Device save(Device device, Long userId) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        device.getUsers().clear();
        device.getUsers().add(user);
        return deviceRepository.save(device);
    }

    public List<Device> findAllByUserId(Long userId) {
        return deviceRepository.findByUserId(userId);
    }

    public Device findByIdAndUser(Long id, Long userId) {
        return deviceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));
    }

    public Device updateDevice(Long id, Device updated, Long userId) {
        Device device = findByIdAndUser(id, userId);

        device.setName(updated.getName());
        device.setLocation(updated.getLocation());
        if (updated.getStatus() != null) {
            device.setStatus(updated.getStatus());
        }
        return deviceRepository.save(device);
    }

    public void deleteDevice(Long id, Long userId) {
        Device device = findByIdAndUser(id, userId);
        deviceRepository.delete(device);
    }

    public Device updateStatus(String deviceCode, String status, Long userId) {
        Device device = deviceRepository.findByDeviceCodeAndUserId(deviceCode, userId);
        if (device == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found");
        device.setStatus(status);
        return deviceRepository.save(device);
    }

    public Device findByDeviceCode(String deviceCode) {
        return deviceRepository.findByDeviceCode(deviceCode);
    }

    public List<UserAccount> getUsersOfDevice(Long deviceId) {
        Device device = deviceRepository.findByIdWithUsers(deviceId);
        return device.getUsers();
    }

    public Page<Device> filter(
            String deviceCode,
            String name,
            String location,
            String status,
            Long userId,
            Pageable pageable
    ) {

        List<Specification<Device>> specs = new ArrayList<>();

        if (deviceCode != null && !deviceCode.isBlank()) {
            specs.add((root, query, cb) ->
                    cb.like(
                            cb.lower(root.get("deviceCode")),
                            "%" + deviceCode.toLowerCase() + "%"
                    )
            );
        }

        if (name != null && !name.isBlank()) {
            specs.add((root, query, cb) ->
                    cb.like(
                            cb.lower(root.get("name")),
                            "%" + name.toLowerCase() + "%"
                    )
            );
        }

        if (location != null && !location.isBlank()) {
            specs.add((root, query, cb) ->
                    cb.like(
                            cb.lower(root.get("location")),
                            "%" + location.toLowerCase() + "%"
                    )
            );
        }

        if (status != null && !status.isBlank()) {
            specs.add((root, query, cb) ->
                    cb.equal(root.get("status"), status)
            );
        }

        if (userId != null) {
            specs.add((root, query, cb) -> {
                query.distinct(true);
                return cb.equal(
                        root.join("users").get("id"),
                        userId
                );
            });
        }

        Specification<Device> specification =
                Specification.allOf(specs);

        return deviceRepository.findAll(specification, pageable);
    }

    public List<Device> findAll() {
        return deviceRepository.findAll();
    }

    public Device findById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));
    }

    @Transactional
    public Device updateByAdmin(Long id, Device updated) {
        Device device = findById(id);

        device.setName(updated.getName());
        device.setLocation(updated.getLocation());
        device.setStatus(updated.getStatus());

        return deviceRepository.save(device);
    }

    @Transactional
    public void deleteByAdmin(Long id) {
        Device device = findById(id);
        deviceRepository.delete(device);
    }
}