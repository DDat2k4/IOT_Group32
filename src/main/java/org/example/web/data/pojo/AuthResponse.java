package org.example.web.data.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private Long userId;
    private String username;
    private String role;
    private String accessToken;
    private String refreshToken;
}
