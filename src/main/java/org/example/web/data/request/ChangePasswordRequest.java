package org.example.web.data.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private Long userId;
    private String oldPassword;
    private String newPassword;
}
