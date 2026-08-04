package com.example.demo.user.dto;

import com.example.demo.user.entity.User;
import lombok.Data;

import java.util.UUID;

@Data
public class UserBasicInfoDTO implements UserInfoDTO {
    private UUID id;
    private String name;
    private String email;

    public static UserBasicInfoDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        UserBasicInfoDTO dto = new UserBasicInfoDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
