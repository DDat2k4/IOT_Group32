package org.example.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.web.data.constant.DeviceStatus;
import org.example.web.data.entity.Device;
import org.example.web.data.response.DeviceResponse;
import org.example.web.mapper.DeviceMapper;
import org.example.web.service.DeviceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.example.web.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    // Tạo device
    @PostMapping
    public ResponseEntity<DeviceResponse> createDevice(
            @RequestBody Device device,
            Authentication auth
    ) {
        Long userId = ((CustomUserDetails) auth.getPrincipal()).getId();
        Device saved = deviceService.save(device, userId);
        return ResponseEntity.ok(DeviceMapper.toResponse(saved));
    }

    // Lấy tất cả device của user
    @GetMapping
    public ResponseEntity<List<DeviceResponse>> getAllDevices(Authentication auth) {
        Long userId = ((CustomUserDetails) auth.getPrincipal()).getId();
        List<Device> devices = deviceService.findAllByUserId(userId);
        return ResponseEntity.ok(DeviceMapper.toResponseList(devices));
    }

    // Lấy device theo id
    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponse> getDeviceById(
            @PathVariable Long id,
            Authentication auth
    ) {
        Long userId = ((CustomUserDetails) auth.getPrincipal()).getId();
        Device device = deviceService.findByIdAndUser(id, userId);
        return ResponseEntity.ok(DeviceMapper.toResponse(device));
    }

    // Cập nhật device
    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponse> updateDevice(
            @PathVariable Long id,
            @RequestBody Device updated,
            Authentication auth
    ) {
        Long userId = ((CustomUserDetails) auth.getPrincipal()).getId();
        Device device = deviceService.updateDevice(id, updated, userId);
        return ResponseEntity.ok(DeviceMapper.toResponse(device));
    }

    // Cập nhật status
    @PatchMapping("/{deviceCode}/status")
    public ResponseEntity<DeviceResponse> updateStatus(
            @PathVariable String deviceCode,
            @RequestParam DeviceStatus status,
            Authentication auth
    ) {
        Long userId = ((CustomUserDetails) auth.getPrincipal()).getId();
        Device updated = deviceService.updateStatus(deviceCode, status.name(), userId);
        return ResponseEntity.ok(DeviceMapper.toResponse(updated));
    }

    // Xóa device
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(
            @PathVariable Long id,
            Authentication auth
    ) {
        Long userId = ((CustomUserDetails) auth.getPrincipal()).getId();
        deviceService.deleteDevice(id, userId);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping
//    public ResponseEntity<Page<Device>> filter(
//            @RequestParam(required = false) String deviceCode,
//            @RequestParam(required = false) String name,
//            @RequestParam(required = false) String location,
//            @RequestParam(required = false) String status,
//            @RequestParam(required = false) Long userId,
//            Pageable pageable
//    ) {
//        return ResponseEntity.ok(deviceService.filter(
//                deviceCode, name, location, status, userId, pageable
//        ));
//    }
}