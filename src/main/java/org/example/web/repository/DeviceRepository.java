package org.example.web.repository;

import org.example.web.data.entity.Device;
import org.example.web.data.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    boolean existsByDeviceCode(String deviceCode);
    // Lấy tất cả device của user
    @Query("SELECT d FROM Device d JOIN d.users u WHERE u.id = :userId")
    List<Device> findByUserId(@Param("userId") Long userId);

    // Lấy device theo id và user
    @Query("SELECT d FROM Device d JOIN d.users u WHERE d.id = :deviceId AND u.id = :userId")
    Optional<Device> findByIdAndUserId(@Param("deviceId") Long deviceId, @Param("userId") Long userId);

    // Lấy device theo deviceCode và user
    @Query("SELECT d FROM Device d JOIN d.users u WHERE d.deviceCode = :deviceCode AND u.id = :userId")
    Device findByDeviceCodeAndUserId(@Param("deviceCode") String deviceCode, @Param("userId") Long userId);

    // Lấy device theo deviceCode
    Device findByDeviceCode(String deviceCode);

    // Lấy danh sách user của device
    @Query("SELECT u FROM Device d JOIN d.users u WHERE d.id = :deviceId")
    List<UserAccount> findUsersByDeviceId(@Param("deviceId") Long deviceId);

    @Query("SELECT d FROM Device d LEFT JOIN FETCH d.users WHERE d.id = :id")
    Device findByIdWithUsers(@Param("id") Long id);
}

