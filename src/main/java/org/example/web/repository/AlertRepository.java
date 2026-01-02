package org.example.web.repository;

import org.example.web.data.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByDeviceId(Long deviceId);

    List<Alert> findBySensorId(Long sensorId);

    List<Alert> findByUserId(Long userId);
}

