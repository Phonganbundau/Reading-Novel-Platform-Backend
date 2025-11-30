package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Story;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class StoryGiftDTO {
    private Long id;
    private String title;
    private String description;
    private String coverUrl;
    private String genre;
    private String author;
    private Integer chapterCount;
    private Long viewCount;
    private BigDecimal giftAmount;

    public static StoryGiftDTO fromEntity(Story story, BigDecimal giftAmount) {
        StoryGiftDTO dto = new StoryGiftDTO();
        dto.setId(story.getId());
        dto.setTitle(story.getTitle());
        dto.setDescription(story.getDescription());
        dto.setCoverUrl(story.getCoverUrl());
        dto.setGenre(story.getGenre().getName());
        dto.setAuthor(story.getAuthor());
        dto.setChapterCount(story.getChapterCount());
        dto.setViewCount(story.getViewCount());
        dto.setGiftAmount(giftAmount);
        return dto;
    }
} 