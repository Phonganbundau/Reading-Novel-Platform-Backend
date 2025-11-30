package com.truyenchu.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "reference_id")
    private Long referenceId; // ID of the related entity (story, comment, etc.)

    @Column(name = "story_id")
    private Long storyId;

    private boolean isRead = false;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum NotificationType {
        NEW_CHAPTER,
        STORY_UPDATED,
        COMMENT_REPLY,
        STORY_FOLLOWED,
        PLAZA_REPLY,
        WALL_COMMENT_REPLY,
        WALL_COMMENT_POST,
        CHAPTER_PURCHASED,
        SYSTEM_MESSAGE
    }
} 