package org.example.web.repository;

import org.example.web.data.entity.AlertEmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlertEmailLogRepository extends JpaRepository<AlertEmailLog, Long> {

    Optional<AlertEmailLog> findByUserIdAndSensorId(Long userId, Long sensorId);

}
