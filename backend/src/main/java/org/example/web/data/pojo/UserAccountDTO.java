package org.example.web.data.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountDTO {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String role;
}

