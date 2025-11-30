package org.example.web.repository;

import org.example.web.data.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Device findByDeviceCode(String deviceCode);
    Boolean existsByDeviceCode(String deviceCode);
}

