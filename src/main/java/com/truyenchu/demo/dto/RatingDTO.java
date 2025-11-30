package com.truyenchu.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RatingDTO {
    private Long id;
    private int rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String username;
    private String userAvatar;
    private Long storyId;
    private String comment;
} 