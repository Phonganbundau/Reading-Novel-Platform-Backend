package com.truyenchu.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.truyenchu.demo.entity.Plaza;
import com.truyenchu.demo.entity.User;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PlazaDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String username;
    private String userAvatar;
    private String userTitle;
    private Long storyId;
    private String storyTitle;
    private String storyCoverUrl;
    private Long parentPlazaId;
    private Long rootPlazaId;
    private List<PlazaDTO> replies;
    private Long likesCount;
    private Long repliesCount;
    @JsonProperty("isLiked")
    private boolean isLiked; // Người dùng hiện tại đã like chưa
    private int level; // Cấp độ của plaza (1: gốc, 2: reply, 3: reply của reply...)

    public static PlazaDTO fromEntity(Plaza plaza) {
        return fromEntity(plaza, null);
    }
    
    public static PlazaDTO fromEntity(Plaza plaza, User currentUser) {
        PlazaDTO dto = new PlazaDTO();
        dto.setId(plaza.getId());
        dto.setContent(plaza.getContent());
        dto.setCreatedAt(plaza.getCreatedAt());
        dto.setUpdatedAt(plaza.getUpdatedAt());
        dto.setUserId(plaza.getUser().getId());
        dto.setUsername(plaza.getUser().getUsername());
        dto.setUserAvatar(plaza.getUser().getAvatar());
        dto.setUserTitle(plaza.getUser().getTitle() != null ? plaza.getUser().getTitle().getName() : null);
        dto.setLikesCount(plaza.getLikesCount());
        dto.setRepliesCount(plaza.getRepliesCount());
        dto.setLiked(false); // Default value, will be set by service layer
        
        if (plaza.getStory() != null) {
            dto.setStoryId(plaza.getStory().getId());
            dto.setStoryTitle(plaza.getStory().getTitle());
            dto.setStoryCoverUrl(plaza.getStory().getCoverUrl());
        }
        
        if (plaza.getParentPlaza() != null) {
            dto.setParentPlazaId(plaza.getParentPlaza().getId());
        }
        
        if (plaza.getRootPlaza() != null) {
            dto.setRootPlazaId(plaza.getRootPlaza().getId());
        }
        
        // Tính cấp độ của plaza
        dto.setLevel(calculateLevel(plaza));
        
        // Chỉ hiển thị replies trực tiếp (không phải tất cả replies của replies)
        if (plaza.getReplies() != null && !plaza.getReplies().isEmpty()) {
            dto.setReplies(plaza.getReplies().stream()
                .map(PlazaDTO::fromEntity)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    private static int calculateLevel(Plaza plaza) {
        int level = 1;
        Plaza current = plaza;
        while (current.getParentPlaza() != null) {
            level++;
            current = current.getParentPlaza();
        }
        return level;
    }
} 