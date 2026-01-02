package com.truyenchu.demo.service;

import com.truyenchu.demo.config.RabbitMQConfig;
import com.truyenchu.demo.dto.NewChapterNotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Gửi message vào queue để xử lý notification cho chapter mới
     */
    public void sendNewChapterNotification(NewChapterNotificationMessage message) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.NOTIFICATION_EXCHANGE,
                    "new-chapter",
                    message
            );
            log.info("Sent new chapter notification message for storyId: {}, followerId: {}", 
                    message.getStoryId(), message.getFollowerId());
        } catch (Exception e) {
            log.error("Failed to send new chapter notification message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send notification message", e);
        }
    }
}

