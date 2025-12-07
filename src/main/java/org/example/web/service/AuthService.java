package org.example.web.service;

import org.example.web.data.entity.PasswordResetToken;
import org.example.web.data.request.RegisterRequest;
import org.example.web.repository.PasswordResetTokenRepository;
import org.example.web.service.mail.MailService;
import org.springframework.security.authentication.BadCredentialsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.web.data.entity.UserAccount;
import org.example.web.data.entity.UserRefreshToken;
import org.example.web.data.pojo.AuthResponse;
import org.example.web.repository.UserAccountRepository;
import org.example.web.repository.UserRefreshTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public AuthResponse login(String username, String rawPassword) {
        UserAccount user = userAccountRepository
                .findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // Clear old tokens
        logoutAll(user.getId());

        String accessToken = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getUsername());

        saveRefreshToken(user, refreshToken);

        return AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        // Kiểm tra username đã tồn tại chưa
        if (userAccountRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BadCredentialsException("Username already exists");
        }

        // Tạo user mới
        UserAccount user = UserAccount.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullname())
                .role("USER")
                .build();

        userAccountRepository.save(user);

        // Tạo access + refresh token ngay sau khi đăng ký
        String accessToken = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getUsername());
        saveRefreshToken(user, refreshToken);

        return AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        UserRefreshToken token = userRefreshTokenRepository
                .findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BadCredentialsException("Invalid or expired refresh token"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Refresh token expired");
        }

        UserAccount user = userAccountRepository
                .findById(token.getUserId())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        String newAccessToken = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole());

        log.info("Refresh token used by {}", user.getUsername());

        return AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void logout(String refreshToken) {
        if (userRefreshTokenRepository.findByRefreshToken(refreshToken).isEmpty()) {
            throw new BadCredentialsException("Refresh token not found");
        }

        userRefreshTokenRepository.deleteByRefreshToken(refreshToken);

        log.info("Refresh token revoked");
    }

    public void logoutAll(Long userId) {
        userRefreshTokenRepository.deleteByUserId(userId);
        log.info("All refresh tokens removed for userId={}", userId);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        UserAccount user = userAccountRepository
                .findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadCredentialsException("Old password incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userAccountRepository.save(user);

        logoutAll(userId); // revoke all sessions
        log.info("Password changed. Revoked all refresh tokens for user {}", user.getUsername());
    }

    private void saveRefreshToken(UserAccount user, String refreshToken) {
        UserRefreshToken token = UserRefreshToken.builder()
                .userId(user.getId())
                .refreshToken(refreshToken)
                .userAgent("unknown")
                .ipAddress("unknown")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        userRefreshTokenRepository.save(token);
    }

    public void sendResetCode(String email) {

        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Email not found"));

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        PasswordResetToken token = PasswordResetToken.builder()
                .userId(user.getId())
                .otp(otp)
                .expireAt(LocalDateTime.now().plusMinutes(10))
                .build();

        passwordResetTokenRepository.save(token);

        mailService.sendResetOtp(email, otp);
    }

    public void resetPassword(String email, String otp, String newPassword) {

        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Email not found"));

        PasswordResetToken token = passwordResetTokenRepository
                .findTopByUserIdOrderByExpireAtDesc(user.getId())
                .orElseThrow(() -> new BadCredentialsException("OTP not found"));

        if (!token.getOtp().equals(otp)) {
            throw new BadCredentialsException("Invalid OTP");
        }

        if (token.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("OTP expired");
        }

        // Đổi mật khẩu
        user.setPassword(passwordEncoder.encode(newPassword));
        userAccountRepository.save(user);

        // Xoá OTP sau khi dùng
        passwordResetTokenRepository.delete(token);

        // Gửi email xác nhận
        mailService.sendPasswordChangedNotification(email);

        log.info("Password reset successful for {}", email);
    }
}
