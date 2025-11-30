package org.example.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.web.data.entity.Device;
import org.example.web.data.response.DeviceResponse;
import org.example.web.mapper.DeviceMapper;
import org.example.web.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    public ResponseEntity<DeviceResponse> createDevice(@RequestBody Device device) {
        Device saved = deviceService.save(device);
        return ResponseEntity.ok(DeviceMapper.toResponse(saved));
    }

    @GetMapping
    public ResponseEntity<List<DeviceResponse>> getAllDevices() {
        List<DeviceResponse> list = deviceService.findAll()
                .stream()
                .map(DeviceMapper::toResponse)
                .toList();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponse> getDeviceById(@PathVariable Long id) {
        return deviceService.findById(id)
                .map(device -> ResponseEntity.ok(DeviceMapper.toResponse(device)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{deviceCode}")
    public ResponseEntity<DeviceResponse> getByDeviceCode(@PathVariable String deviceCode) {
        Device device = deviceService.findByDeviceCode(deviceCode);
        if (device == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(DeviceMapper.toResponse(device));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponse> updateDevice(
            @PathVariable Long id,
            @RequestBody Device updated
    ) {
        try {
            Device device = deviceService.updateDevice(id, updated);
            return ResponseEntity.ok(DeviceMapper.toResponse(device));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{deviceCode}/status")
    public ResponseEntity<DeviceResponse> updateStatus(
            @PathVariable String deviceCode,
            @RequestParam String status
    ) {
        try {
            Device updated = deviceService.updateStatus(deviceCode, status);
            return ResponseEntity.ok(DeviceMapper.toResponse(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        try {
            deviceService.deleteDevice(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}