package org.example.web.mapper;

import org.example.web.data.entity.UserAccount;
import org.example.web.data.pojo.UserAccountDTO;

public class UserAccountMapper {

    public static UserAccountDTO toDTO(UserAccount user) {
        if (user == null) return null;

        UserAccountDTO dto = new UserAccountDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());

        return dto;
    }

    public static void updateEntity(UserAccount user, UserAccountDTO dto) {
        if (user == null || dto == null) return;

        user.setUsername(dto.getUsername());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
    }
}
