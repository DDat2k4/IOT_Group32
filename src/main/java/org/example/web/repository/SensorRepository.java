package org.example.web.repository;

import org.example.web.data.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    List<Sensor> findByDeviceId(Long deviceId);
    boolean existsByDeviceIdAndSensorType(Long deviceId, String sensorType);
    // Tìm sensor theo deviceId và sensorType
    Optional<Sensor> findByDeviceIdAndSensorType(Long deviceId, String sensorType);

    Sensor findTopByDeviceDeviceCodeOrderByIdAsc(String deviceCode);
}
