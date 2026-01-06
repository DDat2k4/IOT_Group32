package org.example.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.web.data.entity.Device;
import org.example.web.data.response.DeviceResponse;
import org.example.web.mapper.DeviceMapper;
import org.example.web.service.DeviceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/devices")
@RequiredArgsConstructor
public class AdminDeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public ResponseEntity<List<DeviceResponse>> getAll() {
        return ResponseEntity.ok(
                DeviceMapper.toResponseList(deviceService.findAll())
        );
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<DeviceResponse>> filter(
            @RequestParam(required = false) String deviceCode,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId,
            Pageable pageable
    ) {
        Page<Device> page = deviceService.filter(
                deviceCode, name, location, status, userId, pageable
        );
        return ResponseEntity.ok(page.map(DeviceMapper::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponse> getById(@PathVariable Long id) {
        Device device = deviceService.findById(id);
        return ResponseEntity.ok(DeviceMapper.toResponse(device));
    }


    @GetMapping("/{id}/users")
    public ResponseEntity<?> getUsersOfDevice(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.getUsersOfDevice(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponse> update(
            @PathVariable Long id,
            @RequestBody Device device
    ) {
        Device updated = deviceService.updateByAdmin(id, device);
        return ResponseEntity.ok(DeviceMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deviceService.deleteByAdmin(id);
        return ResponseEntity.noContent().build();
    }
}

