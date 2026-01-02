package org.example.web.service;

import lombok.RequiredArgsConstructor;
import org.example.web.data.entity.Alert;
import org.example.web.data.response.AlertResponse;
import org.example.web.mapper.AlertMapper;
import org.example.web.repository.AlertRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepo;
    private final AlertMapper mapper; // map Alert → AlertResponse

    public List<AlertResponse> getAll() {
        return alertRepo.findAll()
                .stream().map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<AlertResponse> getByDevice(Long deviceId) {
        int limit = 50;
        return alertRepo.findByDeviceIdOrderByCreatedAtDesc(deviceId, PageRequest.of(0, limit))
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<AlertResponse> getByUserId(Long userId) {
        int limit = 50;
        return alertRepo.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, limit))
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<AlertResponse> getBySensor(Long sensorId) {
        return alertRepo.findBySensorId(sensorId)
                .stream().map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public AlertResponse getOne(Long id) {
        return alertRepo.findById(id)
                .map(mapper::toResponse)
                .orElse(null);
    }

    public boolean delete(Long id) {
        if (!alertRepo.existsById(id)) return false;
        alertRepo.deleteById(id);
        return true;
    }

    // Hàm ghi log alert (tự động gọi khi nhận MQTT hoặc vượt ngưỡng)
    public void logAlert(Alert alert) {
        alertRepo.save(alert);
    }
}



