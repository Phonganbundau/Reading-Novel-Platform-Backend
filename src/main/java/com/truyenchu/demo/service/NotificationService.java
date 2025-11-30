package com.truyenchu.demo.service;

import com.truyenchu.demo.entity.Comment;
import com.truyenchu.demo.entity.Notification;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.entity.WallComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    Notification createNotification(User user, String message, Notification.NotificationType type, Long storyId, Long referenceId);
    void markAsRead(Long notificationId);
    void markAllAsRead(User user);
    void deleteNotification(Long notificationId);
    void deleteAllNotifications(User user);
    Notification findById(Long id);
    Page<Notification> findByUser(User user, Pageable pageable);
    Page<Notification> findUnreadByUser(User user, Pageable pageable);
    long countUnreadByUser(User user);
    void createNewChapterNotification(User user, Long storyId, String storyTitle, Integer chapterNumber, String chapterTitle);
    void createCommentReplyNotification(User recipient, User sender, Comment parentComment, Comment reply);
    
    // Wall comment notifications
    void createWallCommentPostNotification(User recipient, User sender, WallComment wallComment);
    void createWallCommentReplyNotification(User recipient, User sender, WallComment parentComment, WallComment reply);
    void createPlazaReplyNotification(
        com.truyenchu.demo.entity.User receiver,
        com.truyenchu.demo.entity.User sender,
        com.truyenchu.demo.entity.Plaza parentPlaza,
        com.truyenchu.demo.entity.Plaza replyPlaza,
        Long referenceId
    );
} 