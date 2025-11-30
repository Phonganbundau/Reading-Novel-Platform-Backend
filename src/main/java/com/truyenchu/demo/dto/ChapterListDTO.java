package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Chapter;
import com.truyenchu.demo.entity.Tag;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ChapterListDTO {
    private Long id;
    private Integer chapterNumber;
    private String title;
    private Long storyId;
    private String storyTitle;
    private String coverUrl;
    private String author;
    private boolean locked;
    private BigDecimal price;
    private boolean isUnlocked;
    private String genre;
    private Set<String> tags;
    private boolean isReadable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ChapterListDTO fromEntity(Chapter chapter) {
        ChapterListDTO dto = new ChapterListDTO();
        dto.setId(chapter.getId());
        dto.setChapterNumber(chapter.getChapterNumber());
        dto.setStoryId(chapter.getStory().getId());
        dto.setTitle(chapter.getTitle());
        dto.setLocked(chapter.isLocked());
        dto.setPrice(chapter.getPrice());
        dto.setUnlocked(false);
        dto.setReadable(!chapter.isLocked());
        dto.setCreatedAt(chapter.getCreatedAt());
        dto.setUpdatedAt(chapter.getUpdatedAt());
        dto.setStoryTitle(chapter.getStory().getTitle());
        dto.setCoverUrl(chapter.getStory().getCoverUrl());
        dto.setAuthor(chapter.getStory().getAuthor());
        dto.setGenre(chapter.getStory().getGenre().getName());
        dto.setTags(chapter.getStory().getTags().stream().map(Tag::getName).collect(Collectors.toSet()));
        return dto;
    }
} 