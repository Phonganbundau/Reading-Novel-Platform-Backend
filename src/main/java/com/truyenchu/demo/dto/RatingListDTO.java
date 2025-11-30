package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Rating;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RatingListDTO {
    private Long id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // User information
    private Long userId;
    private String username;
    private String userAvatar;
    
    // Story information
    private Long storyId;
    private String storyTitle;
    private String storyCoverUrl;
    private String storyGenre;
    private String storyAuthor;

    public static RatingListDTO fromEntity(Rating rating) {
        RatingListDTO dto = new RatingListDTO();
        dto.setId(rating.getId());
        dto.setRating(rating.getRating());
        dto.setComment(rating.getComment());
        dto.setCreatedAt(rating.getCreatedAt());
        dto.setUpdatedAt(rating.getUpdatedAt());
        
        // User info
        dto.setUserId(rating.getUser().getId());
        dto.setUsername(rating.getUser().getUsername());
        dto.setUserAvatar(rating.getUser().getAvatar());
        
        // Story info
        dto.setStoryId(rating.getStory().getId());
        dto.setStoryTitle(rating.getStory().getTitle());
        dto.setStoryCoverUrl(rating.getStory().getCoverUrl());
        dto.setStoryGenre(rating.getStory().getGenre().getName());
        dto.setStoryAuthor(rating.getStory().getAuthor());
        
        return dto;
    }
} 