package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Chapter;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ChapterContentDTO {
    private Long id;
    private Long storyId;
    private String storyTitle;
    private Integer chapterNumber;
    private String title;
    private String content;
    private boolean locked;
    private BigDecimal price;
    private boolean isUnlocked;
    private boolean isReadable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ChapterContentDTO fromEntity(Chapter chapter, boolean isUnlocked) {
        ChapterContentDTO dto = new ChapterContentDTO();
        dto.setId(chapter.getId());
        dto.setStoryId(chapter.getStory().getId());
        dto.setStoryTitle(chapter.getStory().getTitle());
        dto.setChapterNumber(chapter.getChapterNumber());
        dto.setTitle(chapter.getTitle());
        dto.setContent(chapter.getContent());
        dto.setLocked(chapter.isLocked());
        dto.setPrice(chapter.getPrice());
        dto.setUnlocked(isUnlocked);
        dto.setReadable(!chapter.isLocked() || isUnlocked);
        dto.setCreatedAt(chapter.getCreatedAt());
        dto.setUpdatedAt(chapter.getUpdatedAt());
        return dto;
    }
} 