package org.example.web.repository;

import org.example.web.data.entity.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {

    // Lấy tất cả token theo userId (multi-device)
    List<UserRefreshToken> findByUserId(Long userId);

    // Lấy token chính xác
    Optional<UserRefreshToken> findByRefreshToken(String refreshToken);

    // Xoá 1 token
    void deleteByRefreshToken(String refreshToken);

    // Xoá tất cả token của user (logout all devices)
    void deleteByUserId(Long userId);
}
