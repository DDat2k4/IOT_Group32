package org.example.web.controller;

import lombok.RequiredArgsConstructor;
import org.example.web.data.request.SensorRequest;
import org.example.web.data.response.SensorResponse;
import org.example.web.service.SensorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {

    private final SensorService sensorService;

    @PostMapping("/device/{deviceId}")
    public SensorResponse create(
            @PathVariable Long deviceId,
            @RequestBody SensorRequest req) {
        return sensorService.createSensor(deviceId, req);
    }

    @GetMapping("/device/{deviceId}")
    public List<SensorResponse> getByDevice(@PathVariable Long deviceId) {
        return sensorService.getByDevice(deviceId);
    }

    @PutMapping("/{id}")
    public SensorResponse update(@PathVariable Long id, @RequestBody SensorRequest req) {
        return sensorService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        sensorService.delete(id);
    }
}

