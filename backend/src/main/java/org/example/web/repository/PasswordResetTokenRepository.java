package org.example.web.repository;

import org.example.web.data.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByUserIdAndOtp(Long userId, String otp);

    void deleteByUserId(Long userId);

    Optional<PasswordResetToken> findTopByUserIdOrderByExpireAtDesc(Long userId);
}
