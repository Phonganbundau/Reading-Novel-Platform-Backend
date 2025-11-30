package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.repository.ChapterUnlockRepository;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class StoryDetailDTO {
    private Long id;
    private String title;
    private String coverUrl;
    private String author;
    private String status;
    private String description;
    private GenreDTO genre;
    private Set<TagDTO> tags;
    private List<ChapterListDTO> chapters;
    private UserDTO translator;
    private int chapterCount;
    private Long likeCount;
    private Long viewCount;
    private BigDecimal rating;
    private LocalDateTime updatedAt;
    private boolean isVip;
    private boolean isFree;

    public static StoryDetailDTO fromEntity(Story story, User currentUser, ChapterUnlockRepository chapterUnlockRepository) {
        StoryDetailDTO dto = new StoryDetailDTO();
        dto.setId(story.getId());
        dto.setTitle(story.getTitle());
        dto.setCoverUrl(story.getCoverUrl());
        dto.setAuthor(story.getAuthor());
        dto.setStatus(story.getStatus().name());
        dto.setDescription(story.getDescription());
        dto.setGenre(GenreDTO.fromEntity(story.getGenre()));
        
        // Safely convert tags using a new HashSet
        Set<TagDTO> tagDTOs = new HashSet<>();
        if (story.getTags() != null) {
            tagDTOs = story.getTags().stream()
                .map(TagDTO::fromEntity)
                .collect(Collectors.toSet());
        }
        dto.setTags(tagDTOs);
        
        // Safely convert chapters using a new ArrayList and ChapterListDTO
        List<ChapterListDTO> chapterDTOs = new ArrayList<>();
        if (story.getChapters() != null) {
            // Get all unlocked chapters for this user and story
            final Set<Long> unlockedChapterIds = new HashSet<>();
            if (currentUser != null) {
                unlockedChapterIds.addAll(
                    chapterUnlockRepository.findByUserAndChapter_Story(currentUser, story)
                        .stream()
                        .map(unlock -> unlock.getChapter().getId())
                        .collect(Collectors.toSet())
                );
            }
            
            chapterDTOs = story.getChapters().stream()
                .map(chapter -> {
                    ChapterListDTO chapterDTO = ChapterListDTO.fromEntity(chapter);
                    boolean isUnlocked = unlockedChapterIds.contains(chapter.getId());
                    chapterDTO.setUnlocked(isUnlocked);
                    // Chapter có thể đọc nếu miễn phí hoặc đã mở khóa
                    chapterDTO.setReadable(!chapter.isLocked() || isUnlocked);
                    return chapterDTO;
                })
                .collect(Collectors.toList());
        }
        dto.setChapters(chapterDTOs);
        
        // Safely handle translator
        if (story.getTranslator() != null) {
            dto.setTranslator(UserDTO.fromEntity(story.getTranslator()));
        }
        
        dto.setChapterCount(story.getChapters().size());
        dto.setLikeCount(story.getVoteCount());
        dto.setViewCount(story.getViewCount());
        dto.setRating(story.getRating());
        dto.setUpdatedAt(story.getUpdatedAt());
        dto.setVip(story.isVip());
        dto.setFree(story.isFree());
        return dto;
    }
} 