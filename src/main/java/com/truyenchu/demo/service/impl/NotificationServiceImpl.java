package com.truyenchu.demo.service.impl;

import com.truyenchu.demo.dto.NotificationDTO;
import com.truyenchu.demo.entity.Comment;
import com.truyenchu.demo.entity.Notification;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.entity.WallComment;
import com.truyenchu.demo.entity.Plaza;
import com.truyenchu.demo.repository.NotificationRepository;
import com.truyenchu.demo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public Notification createNotification(User user, String message, Notification.NotificationType type, Long storyId, Long referenceId) {
        System.out.println("=== DEBUG: Creating notification ===");
        System.out.println("User: " + user.getUsername());
        System.out.println("Message: " + message);
        System.out.println("Type: " + type);
        System.out.println("StoryId: " + storyId);
        System.out.println("ReferenceId: " + referenceId);
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setStoryId(storyId);
        notification.setRead(false);
        Notification savedNotification = notificationRepository.save(notification);
        
        System.out.println("=== DEBUG: Notification saved to DB ===");
        System.out.println("Notification ID: " + savedNotification.getId());
        
        // Gửi thông báo real-time qua WebSocket
        System.out.println("=== DEBUG: Sending WebSocket notification ===");
        System.out.println("Sending to user: " + user.getUsername());
        System.out.println("Destination: /user/" + user.getUsername() + "/queue/notifications/new");

        messagingTemplate.convertAndSendToUser(user.getUsername(), "/queue/test", "Hello to testuser!");

        messagingTemplate.convertAndSend( "/topic/test", "Hello all user here!");
        try {   
            NotificationDTO notificationDTO = NotificationDTO.fromEntity(savedNotification);
            System.out.println("NotificationDTO: " + notificationDTO);
            
            messagingTemplate.convertAndSendToUser(
                user.getUsername(),
                "/queue/notifications/new",
                notificationDTO
            );
            
            System.out.println("=== DEBUG: WebSocket message sent successfully ===");
        } catch (Exception e) {
            System.err.println("=== ERROR: Failed to send WebSocket notification ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return savedNotification;
    }

    @Override
    public void createNewChapterNotification(User user, Long storyId, String storyTitle, Integer chapterNumber, String chapterTitle) {
        Long referenceId = (long) chapterNumber;
        createNotification(user, "Truyện " + storyTitle + " có chương mới: " + chapterTitle, Notification.NotificationType.NEW_CHAPTER, storyId, referenceId);
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = findById(notificationId);
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(User user) {
        notificationRepository.findByUserOrderByCreatedAtDesc(user, Pageable.unpaged())
                .forEach(notification -> {
                    notification.setRead(true);
                    notificationRepository.save(notification);
                });
    }

    @Override
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Override
    public void deleteAllNotifications(User user) {
        notificationRepository.deleteByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Notification findById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> findByUser(User user, Pageable pageable) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> findUnreadByUser(User user, Pageable pageable) {
        return notificationRepository.findUnreadByUser(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnreadByUser(User user) {
        return notificationRepository.countUnreadByUser(user);
    }

    @Override
    public void createCommentReplyNotification(User recipient, User sender, Comment parentComment, Comment reply) {
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setMessage(sender.getUsername() + " đã trả lời bình luận của bạn!");
        notification.setType(Notification.NotificationType.COMMENT_REPLY);
        notification.setReferenceId(reply.getRootComment().getId());
        notification.setStoryId(parentComment.getStory().getId());
        notification.setCreatedAt(java.time.LocalDateTime.now());
        Notification savedNotification = notificationRepository.save(notification);
        
        // Gửi thông báo real-time qua WebSocket
        messagingTemplate.convertAndSendToUser(
            recipient.getUsername(),
            "/queue/notifications/new",
            NotificationDTO.fromEntity(savedNotification)
        );
    }

    @Override
    public void createWallCommentPostNotification(User recipient, User sender, WallComment wallComment) {
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setMessage(sender.getUsername() + " đã đăng bình luận trên tường của bạn!");
        notification.setType(Notification.NotificationType.WALL_COMMENT_POST);
        notification.setReferenceId(recipient.getId());
        notification.setCreatedAt(java.time.LocalDateTime.now());
        Notification savedNotification = notificationRepository.save(notification);
        
        // Gửi thông báo real-time qua WebSocket
        messagingTemplate.convertAndSendToUser(
            recipient.getUsername(),
            "/queue/notifications/new",
            NotificationDTO.fromEntity(savedNotification)
        );
    }

    @Override
    public void createWallCommentReplyNotification(User recipient, User sender, WallComment parentComment, WallComment reply) {
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setMessage(sender.getUsername() + " đã trả lời bình luận của bạn trên tường!");
        notification.setType(Notification.NotificationType.WALL_COMMENT_REPLY);
    
        notification.setReferenceId(parentComment.getUserPost().getId());
        notification.setCreatedAt(java.time.LocalDateTime.now());
        Notification savedNotification = notificationRepository.save(notification);
        
        // Gửi thông báo real-time qua WebSocket
        messagingTemplate.convertAndSendToUser(
            recipient.getUsername(),
            "/queue/notifications/new",
            NotificationDTO.fromEntity(savedNotification)
        );
    }

    @Override
    public void createPlazaReplyNotification(
        User receiver,
        User sender,
        Plaza parentPlaza,
        Plaza replyPlaza,
        Long referenceId
    ) {
        Notification notification = new Notification();
        notification.setUser(receiver);
        notification.setType(Notification.NotificationType.PLAZA_REPLY);
        notification.setMessage(sender.getUsername() + " đã trả lời bình luận của bạn: " + replyPlaza.getContent());
        notification.setReferenceId(referenceId);
        notification.setCreatedAt(java.time.LocalDateTime.now());
        notification.setRead(false);
        notificationRepository.save(notification);
    }
} 