package org.example.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.web.data.response.AlertResponse;
import org.example.web.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    // Lấy toàn bộ alert
    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAll() {
        return ResponseEntity.ok(alertService.getAll());
    }

    // Lấy alert theo deviceId
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<AlertResponse>> getByDevice(@PathVariable Long deviceId) {
        return ResponseEntity.ok(alertService.getByDevice(deviceId));
    }

    // Lấy alert theo sensorId
    @GetMapping("/sensor/{sensorId}")
    public ResponseEntity<List<AlertResponse>> getBySensor(@PathVariable Long sensorId) {
        return ResponseEntity.ok(alertService.getBySensor(sensorId));
    }

    // Lấy alert theo ID
    @GetMapping("/{id}")
    public ResponseEntity<AlertResponse> getOne(@PathVariable Long id) {
        AlertResponse res = alertService.getOne(id);
        return res != null ? ResponseEntity.ok(res) : ResponseEntity.notFound().build();
    }

    // Xóa alert
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        boolean deleted = alertService.delete(id);
        return deleted ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

