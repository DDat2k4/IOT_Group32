package org.example.web.repository;

import org.example.web.data.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    List<Sensor> findByDeviceId(Long deviceId);
    boolean existsByDeviceIdAndSensorType(Long deviceId, String sensorType);

    Optional<Sensor> findByDeviceIdAndSensorType(Long deviceId, String sensorType);

    Sensor findTopByDeviceDeviceCodeOrderByIdAsc(String deviceCode);

    @Modifying
    @Query("""
    update Sensor s
    set s.status = :status
    where s.device.id = :deviceId""")
    void updateStatusByDeviceId(@Param("deviceId") Long deviceId,
                                @Param("status") String status);

}
