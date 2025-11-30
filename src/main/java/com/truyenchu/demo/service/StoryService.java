package com.truyenchu.demo.service;

import com.truyenchu.demo.dto.StoryCreateDTO;
import com.truyenchu.demo.dto.StoryDetailDTO;
import com.truyenchu.demo.dto.StoryListDTO;
import com.truyenchu.demo.dto.StoryVoteDTO;
import com.truyenchu.demo.dto.StoryFollowDTO;
import com.truyenchu.demo.dto.StoryViewDTO;
import com.truyenchu.demo.dto.StoryUnlockDTO;
import com.truyenchu.demo.dto.StoryGiftDTO;
import com.truyenchu.demo.dto.StoryStatsDTO;
import com.truyenchu.demo.dto.StoryFanDTO;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface StoryService {
    Story createStory(StoryCreateDTO storyDTO, String username);
    Story updateStory(Long id, StoryCreateDTO storyDTO, String username);
    void deleteStory(Long id, String username);
    Story getStoryById(Long id);
    Page<Story> getAllStories(Pageable pageable);
    Page<Story> getStoriesByGenre(Long genreId, Pageable pageable);
    Page<Story> getStoriesByTag(Long tagId, Pageable pageable);
    Page<Story> searchStories(String keyword, Pageable pageable);
    Page<Story> findAll(Pageable pageable);
    Page<Story> findByGenre(Long genreId, Pageable pageable);
    Page<Story> searchByTitle(String keyword, Pageable pageable);
    Page<Story> findTopRatedStories(Pageable pageable);
    Page<Story> findMostFollowedStories(Pageable pageable);
    Page<Story> findLatestStories(Pageable pageable);
    void followStory(Long storyId, User user);
    void unfollowStory(Long storyId, User user);
    boolean isFollowing(Long storyId, User user);
    long getFollowerCount(Long storyId);
    List<Story> getRecommendedStories(User user, int limit);

    // DTO methods
    Page<StoryListDTO> getAllStoriesForList(Pageable pageable);
    Page<StoryListDTO> getStoriesByGenreForList(Long genreId, Pageable pageable);
    Page<StoryListDTO> getStoriesByTagForList(Long tagId, Pageable pageable);
    Page<StoryListDTO> searchStoriesForList(String keyword, Pageable pageable);
    StoryDetailDTO getStoryDetail(Long id);
    Page<Story> findByTranslator(User translator, Pageable pageable);
    Page<Story> findByAuthor(String username, Pageable pageable);
    List<Story> searchStoriesSimple(String keyword);
    Page<StoryListDTO> getFreeStories(Pageable pageable);
    Page<StoryListDTO> getFreeStoriesByGenreName(String genreName, Pageable pageable);
    Page<StoryListDTO> getCompletedStoriesByGenreName(String genreName, Pageable pageable);
    Page<StoryListDTO> getCompletedStories(Pageable pageable);
    Page<StoryListDTO> getFeaturedStories(Pageable pageable);
    Page<StoryListDTO> getFeaturedStoriesByGenreName(String genreName, Pageable pageable);
    Page<StoryListDTO> getRecentlyUpdatedStories(Pageable pageable);
    Page<StoryListDTO> getRecentlyUpdatedStoriesByGenre(String genreName, Pageable pageable);
    Page<StoryVoteDTO> getMostVotedStories(Pageable pageable);
    Page<StoryVoteDTO> getMostVotedStoriesByGenre(String genreName, Pageable pageable);
    Page<StoryFollowDTO> getMostFollowedStories(Pageable pageable);
    Page<StoryFollowDTO> getMostFollowedStoriesByGenre(String genreName, Pageable pageable);
    Page<StoryViewDTO> getMostViewedStories(Pageable pageable);
    Page<StoryViewDTO> getMostViewedStoriesByGenre(String genreName, Pageable pageable);
    Page<StoryUnlockDTO> getMostUnlockedStories(boolean last24Hours, Pageable pageable);
    Page<StoryUnlockDTO> getMostUnlockedStoriesByGenre(String genreName, boolean last24Hours, Pageable pageable);
    Page<StoryGiftDTO> getMostGiftedStories(Pageable pageable);
    Page<StoryGiftDTO> getMostGiftedStoriesByGenre(String genreName, Pageable pageable);
    
    // Top ranking methods
    List<Map<String, Object>> getTopTranslatorsByChapterCount(int limit);
    List<Map<String, Object>> getTopUsersByDepositAmount(int limit);
    List<Map<String, Object>> getTopUsersByGiftAmount(int limit);
    Page<StoryListDTO> getVipMostFollowedStories(Pageable pageable);
    Page<StoryListDTO> getVipLatestStories(Pageable pageable);
    Page<StoryListDTO> filterStories(String genreName, Boolean isVip, Boolean isFree, String status, String chapterRange, List<String> tagNames, String keyword, Pageable pageable);
    
    // Thống kê truyện
    StoryStatsDTO getStoryStats(Long storyId);
    Page<StoryStatsDTO> getStoryStatsByTranslator(String username, Pageable pageable);
    
    Page<StoryFanDTO> getStoryFans(Long storyId, Pageable pageable);
} 