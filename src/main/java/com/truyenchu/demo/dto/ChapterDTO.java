package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Chapter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ChapterDTO {
    private Long id;
    
    @NotNull(message = "Story ID is required")
    private Long storyId;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    @NotNull(message = "Chapter number is required")
    @Min(value = 1, message = "Chapter number must be greater than 0")
    private Integer chapterNumber;

    private Integer totalChapters;
    
    
    @Min(value = 0, message = "Coin price cannot be negative")
    private Integer coinPrice = 0;

    private boolean locked;

    private BigDecimal price;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Story info section
    private String storyTitle;
    private String storyAuthor;
    private String storyGenre;
    private String coverUrl;


    public static ChapterDTO fromEntity(Chapter chapter) {
        ChapterDTO dto = new ChapterDTO();
        dto.setId(chapter.getId());
        dto.setTitle(chapter.getTitle());
        dto.setContent(chapter.getContent());
        dto.setChapterNumber(chapter.getChapterNumber());
        dto.setStoryId(chapter.getStory().getId());
        dto.setLocked(chapter.isLocked());
        dto.setTotalChapters(chapter.getStory().getChapterCount());
        dto.setPrice(chapter.getPrice());
        dto.setCreatedAt(chapter.getCreatedAt());
        dto.setUpdatedAt(chapter.getUpdatedAt());
        dto.setStoryTitle(chapter.getStory().getTitle());
        dto.setStoryAuthor(chapter.getStory().getAuthor());
        dto.setStoryGenre(chapter.getStory().getGenre().getName());
        dto.setCoverUrl(chapter.getStory().getCoverUrl());
        return dto;
    }
} 