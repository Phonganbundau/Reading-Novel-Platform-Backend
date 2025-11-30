package com.truyenchu.demo.service.impl;

import com.truyenchu.demo.dto.UserBookDTO;
import com.truyenchu.demo.dto.UserProfileDTO;
import com.truyenchu.demo.dto.UserProfileOtherDTO;
import com.truyenchu.demo.dto.UserStatsDTO;
import com.truyenchu.demo.entity.*;
import com.truyenchu.demo.repository.*;
import com.truyenchu.demo.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    private final UserRepository userRepository;
    private final ChapterUnlockRepository chapterUnlockRepository;
    private final FollowRepository followRepository;
    private final ReadingHistoryRepository readingHistoryRepository;
    private final ChapterRepository chapterRepository;

    @Override
    public UserProfileDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileDTO profile = new UserProfileDTO();
        profile.setId(user.getId());
        profile.setUsername(user.getUsername());
        profile.setAvatar(user.getAvatar());
        profile.setBanner(user.getBanner());
        profile.setTitle(user.getTitle() != null ? user.getTitle().getName() : null);
        profile.setRole(user.getRole().name());
        profile.setCoinBalance(user.getCoinBalance().intValue());
        profile.setVoteTicket(user.getVoteTicket());

        // Get unlocked chapters grouped by story
        Map<Long, List<Long>> unlockedChapters = chapterUnlockRepository.findByUser(user)
            .stream()
            .collect(Collectors.groupingBy(
                unlock -> unlock.getChapter().getStory().getId(),
                Collectors.mapping(unlock -> unlock.getChapter().getId(), Collectors.toList())
            ));
        profile.setUnlockedChapters(unlockedChapters);

        // Get user stats
        UserStatsDTO stats = new UserStatsDTO();
        stats.setBooksRead(readingHistoryRepository.findByUserOrderByLastReadAtDesc(user).size());
        stats.setChaptersRead(user.getChaptersRead());
        stats.setComments(user.getComments().size());
        stats.setReviews(user.getReviews().size());
        profile.setStats(stats);

        // Get saved books (follows)
        List<UserBookDTO> savedBooks = followRepository.findByUser(user)
            .stream()
            .map(this::convertToUserBookDTO)
            .collect(Collectors.toList());
        profile.setSavedBooks(savedBooks);

        // Get reading books (from reading history)
        List<UserBookDTO> readingBooks = readingHistoryRepository.findByUserOrderByLastReadAtDesc(user)
            .stream()
            .map(this::convertToUserBookDTO)
            .collect(Collectors.toList());
        profile.setReadingBooks(readingBooks);

        return profile;
    }

    @Override
    public UserProfileDTO getUserProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return getUserProfile(user.getId());
    }

    private UserBookDTO convertToUserBookDTO(Object entity) {
        UserBookDTO dto = new UserBookDTO();
        Story story;
        LocalDateTime lastReadAt;

        if (entity instanceof Follow) {
            Follow follow = (Follow) entity;
            story = follow.getStory();
            lastReadAt = follow.getCreatedAt();
                    // Get last chapter info
            Chapter lastChapter = chapterRepository.findFirstByStoryOrderByChapterNumberDesc(story)
                .orElse(null);
            if (lastChapter != null) {
                dto.setLastChapter(/* "Chapter " + lastChapter.getChapterNumber() + ": " +*/ lastChapter.getTitle());
            }
        } else if (entity instanceof ReadingHistory) {
            ReadingHistory history = (ReadingHistory) entity;
            story = history.getStory();
            lastReadAt = history.getLastReadAt();
            Chapter chapter = history.getChapter();
            dto.setLastChapter(chapter.getTitle());
            dto.setLastChapterNumber(chapter.getChapterNumber().toString());
        } else {
            throw new IllegalArgumentException("Unsupported entity type");
        }

        dto.setId(story.getId());
        dto.setTitle(story.getTitle());
        dto.setCover(story.getCoverUrl());
        dto.setAuthor(story.getAuthor());
        
        
        dto.setLastRead(lastReadAt.toString());
        return dto;
    }

    @Override
    public UserProfileOtherDTO getUserProfileOther(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileOtherDTO profile = new UserProfileOtherDTO();
        profile.setUsername(user.getUsername());
        profile.setAvatar(user.getAvatar());
        profile.setTitle(user.getTitle() != null ? user.getTitle().getName() : null);
        profile.setBanner(user.getBanner());

        // Get user stats
        UserStatsDTO stats = new UserStatsDTO();
        stats.setBooksRead(readingHistoryRepository.findByUserOrderByLastReadAtDesc(user).size());
        stats.setChaptersRead(user.getChaptersRead());
        stats.setComments(user.getComments().size());
        stats.setReviews(user.getReviews().size());
        profile.setStats(stats);

        return profile;
    }

    @Override
    public UserProfileOtherDTO getUserProfileOtherByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return getUserProfileOther(user.getId());
    }
} 