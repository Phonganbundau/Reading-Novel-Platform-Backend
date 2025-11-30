package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Story;
import lombok.Data;

@Data
public class StoryVoteDTO {
    private Long id;
    private String title;
    private String description;
    private String coverUrl;
    private String genre;
    private String author;
    private Integer chapterCount;
    private Long viewCount;
    private Long voteCount;

    public static StoryVoteDTO fromEntity(Story story, Long voteCount) {
        StoryVoteDTO dto = new StoryVoteDTO();
        dto.setId(story.getId());
        dto.setTitle(story.getTitle());
        dto.setDescription(story.getDescription());
        dto.setCoverUrl(story.getCoverUrl());
        dto.setGenre(story.getGenre().getName());
        dto.setAuthor(story.getAuthor());
        dto.setChapterCount(story.getChapterCount());
        dto.setViewCount(story.getViewCount());
        dto.setVoteCount(voteCount);
        return dto;
    }
} 