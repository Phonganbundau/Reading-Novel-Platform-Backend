package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.User;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String title;
    private String avatar;
    private User.UserRole role;

    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setTitle(user.getTitle() != null ? user.getTitle().getName() : null);
        dto.setAvatar(user.getAvatar());
        dto.setRole(user.getRole());
        return dto;
    }
} 