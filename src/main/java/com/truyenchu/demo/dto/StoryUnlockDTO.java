package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Story;
import lombok.Data;

@Data
public class StoryUnlockDTO {
    private Long id;
    private String title;
    private String description;
    private String coverUrl;
    private String genre;
    private String author;
    private Integer chapterCount;
    private Long viewCount;
    private Long unlockCount;

    public static StoryUnlockDTO fromEntity(Story story, Long unlockCount) {
        StoryUnlockDTO dto = new StoryUnlockDTO();
        dto.setId(story.getId());
        dto.setTitle(story.getTitle());
        dto.setDescription(story.getDescription());
        dto.setCoverUrl(story.getCoverUrl());
        dto.setGenre(story.getGenre().getName());
        dto.setAuthor(story.getAuthor());
        dto.setChapterCount(story.getChapterCount());
        dto.setViewCount(story.getViewCount());
        dto.setUnlockCount(unlockCount);
        return dto;
    }
} 