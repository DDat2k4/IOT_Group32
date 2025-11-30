package org.example.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.web.data.request.SensorRequest;
import org.example.web.data.response.SensorResponse;
import org.example.web.service.SensorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {

    private final SensorService sensorService;

    @PostMapping("/device/{deviceId}")
    public ResponseEntity<SensorResponse> create(
            @PathVariable Long deviceId,
            @RequestBody SensorRequest req) {
        return ResponseEntity.ok(sensorService.createSensor(deviceId, req));
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<SensorResponse>> getByDevice(@PathVariable Long deviceId) {
        return ResponseEntity.ok(sensorService.getByDevice(deviceId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SensorResponse> update(
            @PathVariable Long id,
            @RequestBody SensorRequest req) {
        return ResponseEntity.ok(sensorService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sensorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}