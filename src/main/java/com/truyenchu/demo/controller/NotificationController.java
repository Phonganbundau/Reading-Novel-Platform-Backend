package com.truyenchu.demo.controller;

import com.truyenchu.demo.entity.Notification;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.service.NotificationService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    
    // Test endpoint để gửi notification thủ công
    @PostMapping("/test")
    public ResponseEntity<String> testNotification(@AuthenticationPrincipal User user) {
        try {
            notificationService.createNewChapterNotification(
                user, 
                1L, 
                "Truyện Test", 
                1, 
                "Chương Test"
            );
            return ResponseEntity.ok("Test notification sent successfully to: " + user.getUsername());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @GetMapping
    public ResponseEntity<Page<Notification>> getNotifications(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        return ResponseEntity.ok(notificationService.findByUser(user, pageable));
    }

    @GetMapping("/unread")
    public ResponseEntity<Page<Notification>> getUnreadNotifications(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        return ResponseEntity.ok(notificationService.findUnreadByUser(user, pageable));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadNotificationCount(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(notificationService.countUnreadByUser(user));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(
            @AuthenticationPrincipal User user,
            @PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead(@AuthenticationPrincipal User user) {
        notificationService.markAllAsRead(user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @AuthenticationPrincipal User user,
            @PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllNotifications(@AuthenticationPrincipal User user) {
        notificationService.deleteAllNotifications(user);
        return ResponseEntity.ok().build();
    }
} 