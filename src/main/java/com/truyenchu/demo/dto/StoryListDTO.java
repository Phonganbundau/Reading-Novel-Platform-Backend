package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoryListDTO {
    private Long id;
    private String title;
    private String author;
    private String coverUrl;
    private String description;
    private Integer chapterCount;
    private Long viewCount;
    private Long followCount;
    private BigDecimal rating;
    private boolean isFree;
    private boolean isVip;
    private Story.StoryStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private GenreDTO genre;
    private Set<TagDTO> tags;
    private UserDTO translator;

    public static StoryListDTO fromEntity(Story story) {
        if (story == null) {
            return null;
        }

        StoryListDTO dto = new StoryListDTO();
        dto.setId(story.getId());
        dto.setTitle(story.getTitle());
        dto.setAuthor(story.getAuthor());
        dto.setCoverUrl(story.getCoverUrl());
        dto.setDescription(story.getDescription());
        dto.setChapterCount(story.getChapterCount());
        dto.setViewCount(story.getViewCount());
        dto.setFollowCount(story.getFollowCount());
        dto.setRating(story.getRating());
        dto.setFree(story.isFree());
        dto.setVip(story.isVip());
        dto.setStatus(story.getStatus());
        dto.setCreatedAt(story.getCreatedAt());
        dto.setUpdatedAt(story.getUpdatedAt());

        // Handle genre - defensive copy
        if (story.getGenre() != null) {
            dto.setGenre(GenreDTO.fromEntity(story.getGenre()));
        }

        // Handle tags - create a new HashSet and copy elements safely
        Set<TagDTO> tagDTOs = new HashSet<>();
        if (story.getTags() != null) {
            // Create a defensive copy of the tags set
            Set<Tag> tagsCopy = new HashSet<>(story.getTags()); // Sao chép trước khi stream
            Set<TagDTO> copiedTags = tagsCopy.stream()
                .filter(tag -> tag != null)
                .map(TagDTO::fromEntity)
                .collect(Collectors.toSet());
            tagDTOs.addAll(copiedTags);
        }
        dto.setTags(tagDTOs);

        // Handle translator - defensive copy
        if (story.getTranslator() != null) {
            dto.setTranslator(UserDTO.fromEntity(story.getTranslator()));
        }

        return dto;
    }

    public static List<StoryListDTO> fromEntities(List<Story> stories) {
        if (stories == null) {
            return new ArrayList<>();
        }
        // Create a defensive copy of the input list
        List<Story> storiesCopy = new ArrayList<>(stories);
        return storiesCopy.stream()
                .map(StoryListDTO::fromEntity)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
} 