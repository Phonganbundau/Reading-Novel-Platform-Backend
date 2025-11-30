package com.truyenchu.demo.controller;

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
import com.truyenchu.demo.service.StoryService;
import com.truyenchu.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stories")
@RequiredArgsConstructor
public class StoryController {
    private final StoryService storyService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Story> createStory(@RequestBody StoryCreateDTO storyDTO, Authentication authentication) {
        Story story = storyService.createStory(storyDTO, authentication.getName());
        return ResponseEntity.ok(story);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Story> updateStory(@PathVariable Long id, @RequestBody StoryCreateDTO storyDTO, Authentication authentication) {
        Story story = storyService.updateStory(id, storyDTO, authentication.getName());
        return ResponseEntity.ok(story);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStory(@PathVariable Long id, Authentication authentication) {
        storyService.deleteStory(id, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryDetailDTO> getStoryDetail(@PathVariable Long id) {
        StoryDetailDTO story = storyService.getStoryDetail(id);
        return ResponseEntity.ok(story);
    }

    
    @GetMapping
    public ResponseEntity<Page<StoryListDTO>> getAllStories(Pageable pageable) {
        Page<StoryListDTO> stories = storyService.getAllStoriesForList(pageable);
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/translator/{username}")
    public ResponseEntity<?> getStoriesByTranslator(@PathVariable String username, Pageable pageable) {
        User translator = userService.findByUsername(username);
        Page<Story> stories = storyService.findByTranslator(translator, pageable);
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/author/{username}")
    public ResponseEntity<?> getStoriesByAuthor(@PathVariable String username, Pageable pageable) {
        Page<Story> stories = storyService.findByAuthor(username, pageable);
        return ResponseEntity.ok(stories);
    }

    

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<Page<StoryListDTO>> getStoriesByGenre(@PathVariable Long genreId, Pageable pageable) {
        Page<StoryListDTO> stories = storyService.getStoriesByGenreForList(genreId, pageable);
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/tag/{tagId}")
    public ResponseEntity<Page<StoryListDTO>> getStoriesByTag(@PathVariable Long tagId, Pageable pageable) {
        Page<StoryListDTO> stories = storyService.getStoriesByTagForList(tagId, pageable);
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<StoryListDTO>> searchStories(@RequestParam String keyword, Pageable pageable) {
        Page<StoryListDTO> stories = storyService.searchStoriesForList(keyword, pageable);
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/top-rated")
    public ResponseEntity<?> getTopRatedStories(Pageable pageable) {
        Page<Story> stories = storyService.findTopRatedStories(pageable);
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/most-followed")
    public ResponseEntity<Page<StoryFollowDTO>> getMostFollowedStories(
            @RequestParam(required = false) String genreName,
            Pageable pageable) {
        Page<StoryFollowDTO> stories;
        if (genreName != null && !genreName.trim().isEmpty()) {
            stories = storyService.getMostFollowedStoriesByGenre(genreName, pageable);
        } else {
            stories = storyService.getMostFollowedStories(pageable);
        }
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestStories(Pageable pageable) {
        Page<Story> stories = storyService.findLatestStories(pageable);
        return ResponseEntity.ok(stories);
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<?> followStory(@PathVariable Long id, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        storyService.followStory(id, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/follow")
    public ResponseEntity<?> unfollowStory(@PathVariable Long id, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        storyService.unfollowStory(id, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/is-following")
    public ResponseEntity<?> isFollowing(@PathVariable Long id, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        boolean isFollowing = storyService.isFollowing(id, user);
        return ResponseEntity.ok(isFollowing);
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<?> getFollowerCount(@PathVariable Long id) {
        long count = storyService.getFollowerCount(id);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/search/simple")
    public ResponseEntity<List<Map<String, Object>>> searchStoriesSimple(@RequestParam String keyword) {
        List<Story> stories = storyService.searchStoriesSimple(keyword);
        List<Map<String, Object>> result = stories.stream()
            .map(story -> {
                Map<String, Object> storyMap = new HashMap<>();
                storyMap.put("id", story.getId());
                storyMap.put("title", story.getTitle());
                storyMap.put("author", story.getAuthor());
                storyMap.put("coverUrl", story.getCoverUrl());
                return storyMap;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/free")
    public ResponseEntity<Page<StoryListDTO>> getFreeStories(
            @RequestParam(required = false) String genreName,
            Pageable pageable) {
        Page<StoryListDTO> stories;
        if (genreName != null && !genreName.trim().isEmpty()) {
            stories = storyService.getFreeStoriesByGenreName(genreName, pageable);
        } else {
            stories = storyService.getFreeStories(pageable);
        }
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/completed")
    public ResponseEntity<Page<StoryListDTO>> getCompletedStories(
            @RequestParam(required = false) String genreName,
            Pageable pageable) {
        Page<StoryListDTO> stories;
        if (genreName != null && !genreName.trim().isEmpty()) {
            stories = storyService.getCompletedStoriesByGenreName(genreName, pageable);
        } else {
            stories = storyService.getCompletedStories(pageable);
        }
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/featured")
    public ResponseEntity<Page<StoryListDTO>> getFeaturedStories(@RequestParam(required = false) String genreName, Pageable pageable) {

        Page<StoryListDTO> stories;
        if (genreName != null && !genreName.trim().isEmpty()) {
            stories = storyService.getFeaturedStoriesByGenreName(genreName, pageable);
        } else {
            stories = storyService.getFeaturedStories(pageable);
        }

        return ResponseEntity.ok(stories);
    }

    @GetMapping("/recently-updated")
    public ResponseEntity<Page<StoryListDTO>> getRecentlyUpdatedStories(
            @RequestParam(required = false) String genreName,
            Pageable pageable) {
        Page<StoryListDTO> stories;
        if (genreName != null && !genreName.trim().isEmpty()) {
            stories = storyService.getRecentlyUpdatedStoriesByGenre(genreName, pageable);
        } else {
            stories = storyService.getRecentlyUpdatedStories(pageable);
        }
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/most-voted")
    public ResponseEntity<Page<StoryVoteDTO>> getMostVotedStories(
            @RequestParam(required = false) String genreName,
            Pageable pageable) {
        Page<StoryVoteDTO> stories;
        if (genreName != null && !genreName.trim().isEmpty()) {
            stories = storyService.getMostVotedStoriesByGenre(genreName, pageable);
        } else {
            stories = storyService.getMostVotedStories(pageable);
        }
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/most-viewed")
    public ResponseEntity<Page<StoryViewDTO>> getMostViewedStories(
            @RequestParam(required = false) String genreName,
            Pageable pageable) {
        Page<StoryViewDTO> stories;
        if (genreName != null && !genreName.trim().isEmpty()) {
            stories = storyService.getMostViewedStoriesByGenre(genreName, pageable);
        } else {
            stories = storyService.getMostViewedStories(pageable);
        }
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/most-unlocked")
    public ResponseEntity<Page<StoryUnlockDTO>> getMostUnlockedStories(
            @RequestParam(required = false) String genreName,
            @RequestParam(required = false, defaultValue = "false") boolean last24Hours,
            Pageable pageable) {
        Page<StoryUnlockDTO> stories;
        if (genreName != null && !genreName.trim().isEmpty()) {
            stories = storyService.getMostUnlockedStoriesByGenre(genreName, last24Hours, pageable);
        } else {
            stories = storyService.getMostUnlockedStories(last24Hours, pageable);
        }
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/most-gifted")
    public ResponseEntity<Page<StoryGiftDTO>> getMostGiftedStories(
            @RequestParam(required = false) String genreName,
            Pageable pageable) {
        Page<StoryGiftDTO> stories;
        if (genreName != null && !genreName.trim().isEmpty()) {
            stories = storyService.getMostGiftedStoriesByGenre(genreName, pageable);
        } else {
            stories = storyService.getMostGiftedStories(pageable);
        }
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/my")
    public ResponseEntity<Page<StoryListDTO>> getMyTranslatedStories(Authentication authentication, Pageable pageable) {
        User currentUser = userService.findByUsername(authentication.getName());
        Page<Story> stories = storyService.findByTranslator(currentUser, pageable);
        Page<StoryListDTO> storyListDTOs = stories.map(StoryListDTO::fromEntity);
        return ResponseEntity.ok(storyListDTOs);
    }

    @GetMapping("/top-translators")
    public ResponseEntity<List<Map<String, Object>>> getTopTranslators() {
        List<Map<String, Object>> topTranslators = storyService.getTopTranslatorsByChapterCount(10);
        return ResponseEntity.ok(topTranslators);
    }

    @GetMapping("/top-depositors")
    public ResponseEntity<List<Map<String, Object>>> getTopDepositors() {
        List<Map<String, Object>> topDepositors = storyService.getTopUsersByDepositAmount(10);
        return ResponseEntity.ok(topDepositors);
    }

    @GetMapping("/top-gifters")
    public ResponseEntity<List<Map<String, Object>>> getTopGifters() {
        List<Map<String, Object>> topGifters = storyService.getTopUsersByGiftAmount(10);
        return ResponseEntity.ok(topGifters);
    }

    @GetMapping("/vip-most-followed")
    public ResponseEntity<Page<StoryListDTO>> getVipMostFollowedStories(Pageable pageable) {
        Page<StoryListDTO> stories = storyService.getVipMostFollowedStories(pageable);
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/vip-latest")
    public ResponseEntity<Page<StoryListDTO>> getVipLatestStories(Pageable pageable) {
        Page<StoryListDTO> stories = storyService.getVipLatestStories(pageable);
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<StoryListDTO>> filterStories(
            @RequestParam(required = false) String genreName,
            @RequestParam(required = false) Boolean isVip,
            @RequestParam(required = false) Boolean isFree,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String chapterRange,
            @RequestParam(required = false) List<String> tagNames,
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        Page<StoryListDTO> stories = storyService.filterStories(genreName, isVip, isFree, status, chapterRange, tagNames, keyword, pageable);
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<StoryStatsDTO> getStoryStats(@PathVariable Long id) {
        StoryStatsDTO stats = storyService.getStoryStats(id);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/translator/stats")
    public ResponseEntity<Page<StoryStatsDTO>> getStatsByTranslator(Authentication authentication, Pageable pageable) {
        String username = authentication.getName();
        Page<StoryStatsDTO> stats = storyService.getStoryStatsByTranslator(username, pageable);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{id}/fans")
    public ResponseEntity<Page<StoryFanDTO>> getStoryFans(@PathVariable Long id, Pageable pageable) {
        Page<StoryFanDTO> fans = storyService.getStoryFans(id, pageable);
        return ResponseEntity.ok(fans);
    }
} 