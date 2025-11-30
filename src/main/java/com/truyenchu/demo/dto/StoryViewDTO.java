package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Story;
import lombok.Data;

@Data
public class StoryViewDTO {
    private Long id;
    private String title;
    private String description;
    private String coverUrl;
    private String genre;
    private String author;
    private Integer chapterCount;
    private Long viewCount;

    public static StoryViewDTO fromEntity(Story story) {
        StoryViewDTO dto = new StoryViewDTO();
        dto.setId(story.getId());
        dto.setTitle(story.getTitle());
        dto.setDescription(story.getDescription());
        dto.setCoverUrl(story.getCoverUrl());
        dto.setGenre(story.getGenre().getName());
        dto.setAuthor(story.getAuthor());
        dto.setChapterCount(story.getChapterCount());
        dto.setViewCount(story.getViewCount());
        return dto;
    }
} 