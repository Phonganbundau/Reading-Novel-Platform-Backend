package com.truyenchu.demo.service;

import com.truyenchu.demo.config.RabbitMQConfig;
import com.truyenchu.demo.dto.NewChapterNotificationMessage;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    /**
     * Consumer xử lý message notification cho chapter mới
     */
    @RabbitListener(queues = RabbitMQConfig.NEW_CHAPTER_NOTIFICATION_QUEUE)
    public void handleNewChapterNotification(NewChapterNotificationMessage message) {
        try {
            log.info("Processing new chapter notification: storyId={}, chapterNumber={}, followerId={}", 
                    message.getStoryId(), message.getChapterNumber(), message.getFollowerId());

            // Lấy user từ database
            User follower = userRepository.findById(message.getFollowerId())
                    .orElseThrow(() -> new RuntimeException("User not found: " + message.getFollowerId()));

            // Tạo notification
            notificationService.createNewChapterNotification(
                    follower,
                    message.getStoryId(),
                    message.getStoryTitle(),
                    message.getChapterNumber(),
                    message.getChapterTitle()
            );

            log.info("Successfully created notification for user: {}", follower.getUsername());
        } catch (Exception e) {
            log.error("Error processing new chapter notification: {}", e.getMessage(), e);
            // Re-throw để message được gửi vào DLQ nếu retry hết
            throw new RuntimeException("Failed to process notification", e);
        }
    }
}

