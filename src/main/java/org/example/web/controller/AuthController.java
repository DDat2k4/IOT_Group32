package org.example.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.web.data.entity.UserAccount;
import org.example.web.data.pojo.UserAccountDTO;
import org.example.web.data.response.AuthResponse;
import org.example.web.data.request.*;
import org.example.web.repository.UserAccountRepository;
import org.example.web.service.AuthService;
import org.example.web.data.response.ApiResponse;
import org.example.web.service.UserAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserAccountService userAccountService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(ApiResponse.ok("Register successful", response));
        } catch (BadCredentialsException ex) {
            log.warn("Register failed for username={}", request.getUsername());
            return ResponseEntity.status(400).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
        } catch (BadCredentialsException ex) {
            log.warn("Login failed for username={}", request.getUsername());
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid username or password"));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            AuthResponse response = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(ApiResponse.ok("Token refreshed", response));
        } catch (BadCredentialsException ex) {
            log.warn("Refresh token invalid: {}", request.getRefreshToken());
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid or expired refresh token"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody RefreshTokenRequest request) {
        try {
            authService.logout(request.getRefreshToken());
            return ResponseEntity.ok(ApiResponse.ok("Logout successful", null));
        } catch (BadCredentialsException ex) {
            log.warn("Logout failed. Token not found: {}", request.getRefreshToken());
            return ResponseEntity.status(400).body(ApiResponse.error("Refresh token not found"));
        }
    }

    @PostMapping("/logout-all")
    public ResponseEntity<ApiResponse<Void>> logoutAll(@RequestBody ChangePasswordRequest request,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        UserAccountDTO user = userAccountService.findByUsername(userDetails.getUsername());
        authService.logoutAll(user.getId());
        return ResponseEntity.ok(ApiResponse.ok("All sessions cleared", null));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChangePasswordRequest request
    ) {
        authService.changePassword(
                userDetails.getUsername(),
                request.getOldPassword(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                ApiResponse.ok("Password changed successfully", null)
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.sendResetCode(request.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Reset code sent", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(
                request.getEmail(),
                request.getOtp(),
                request.getNewPassword()
        );
        return ResponseEntity.ok(ApiResponse.ok("Password reset successfully", null));
    }

}
