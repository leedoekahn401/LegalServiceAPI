package com.example.demo.user.dto;

import com.example.demo.user.entity.Role;
import com.example.demo.user.entity.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserFullInfoDTO implements UserInfoDTO {
    private UUID id;
    private String name;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastAccessAt;

    public static UserFullInfoDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        UserFullInfoDTO dto = new UserFullInfoDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setLastAccessAt(user.getLastAccessAt());
        return dto;
    }
}
