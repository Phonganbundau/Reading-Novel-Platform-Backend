package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Story;
import lombok.Data;

@Data
public class StoryFollowDTO {
    private Long id;
    private String title;
    private String description;
    private String coverUrl;
    private String genre;
    private String author;
    private Integer chapterCount;
    private Long viewCount;
    private Long followerCount;

    public static StoryFollowDTO fromEntity(Story story, Long followerCount) {
        StoryFollowDTO dto = new StoryFollowDTO();
        dto.setId(story.getId());
        dto.setTitle(story.getTitle());
        dto.setDescription(story.getDescription());
        dto.setCoverUrl(story.getCoverUrl());
        dto.setGenre(story.getGenre().getName());
        dto.setAuthor(story.getAuthor());
        dto.setChapterCount(story.getChapterCount());
        dto.setViewCount(story.getViewCount());
        dto.setFollowerCount(followerCount);
        return dto;
    }
} 