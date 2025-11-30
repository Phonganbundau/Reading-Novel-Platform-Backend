package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Notification;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NotificationDTO {
    private Long id;
    private String message;
    private Notification.NotificationType type;
    private Long storyId;
    private Long referenceId;
    private boolean isRead;
    private LocalDateTime createdAt;
    private String username;

    public static NotificationDTO fromEntity(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setStoryId(notification.getStoryId());
        dto.setReferenceId(notification.getReferenceId());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        
        // Handle Lazy Loading safely
        try {
            dto.setUsername(notification.getUser().getUsername());
        } catch (Exception e) {
            System.err.println("Warning: Could not get username from notification user: " + e.getMessage());
            dto.setUsername("Unknown");
        }
        
        return dto;
    }
} 