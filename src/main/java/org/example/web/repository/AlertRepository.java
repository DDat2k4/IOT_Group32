package org.example.web.repository;

import org.example.web.data.entity.Alert;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByDeviceIdOrderByCreatedAtDesc(Long deviceId, Pageable pageable);

    List<Alert> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Alert> findBySensorId(Long sensorId);
}

