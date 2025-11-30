package com.truyenchu.demo.controller;

import com.truyenchu.demo.entity.Notification;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class NotificationWebSocketController {
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/notifications.markAsRead")
    public void markNotificationAsRead(@Payload Long notificationId, SimpMessageHeaderAccessor headerAccessor) {
        Authentication auth = (Authentication) headerAccessor.getUser();
        User user = (User) auth.getPrincipal();
        
        Notification notification = notificationService.findById(notificationId);
        if (notification.getUser().equals(user)) {
            notificationService.markAsRead(notificationId);
            
            // Notify user about the update
            messagingTemplate.convertAndSendToUser(
                user.getUsername(),
                "/queue/notifications/read",
                notificationId
            );
        }
    }

    @MessageMapping("/notifications.markAllAsRead")
    public void markAllNotificationsAsRead(SimpMessageHeaderAccessor headerAccessor) {
        Authentication auth = (Authentication) headerAccessor.getUser();
        User user = (User) auth.getPrincipal();
        
        notificationService.markAllAsRead(user);
        
        // Notify user about the update
        messagingTemplate.convertAndSendToUser(
            user.getUsername(),
            "/queue/notifications/allRead",
            null
        );
    }

    @MessageMapping("/notifications.delete")
    public void deleteNotification(@Payload Long notificationId, SimpMessageHeaderAccessor headerAccessor) {
        Authentication auth = (Authentication) headerAccessor.getUser();
        User user = (User) auth.getPrincipal();
        
        Notification notification = notificationService.findById(notificationId);
        if (notification.getUser().equals(user)) {
            notificationService.deleteNotification(notificationId);
            
            // Notify user about the deletion
            messagingTemplate.convertAndSendToUser(
                user.getUsername(),
                "/queue/notifications/deleted",
                notificationId
            );
        }
    }

    @MessageMapping("/notifications.deleteAll")
    public void deleteAllNotifications(SimpMessageHeaderAccessor headerAccessor) {
        Authentication auth = (Authentication) headerAccessor.getUser();
        User user = (User) auth.getPrincipal();
        
        notificationService.deleteAllNotifications(user);
        
        // Notify user about the deletion
        messagingTemplate.convertAndSendToUser(
            user.getUsername(),
            "/queue/notifications/allDeleted",
            null
        );
    }
} 