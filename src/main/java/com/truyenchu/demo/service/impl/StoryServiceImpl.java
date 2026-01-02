package com.truyenchu.demo.service.impl;

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
import com.truyenchu.demo.dto.PageResponse;
import com.truyenchu.demo.entity.Follow;
import com.truyenchu.demo.entity.Genre;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.Story.StoryStatus;
import com.truyenchu.demo.entity.Tag;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.exception.ResourceNotFoundException;
import com.truyenchu.demo.exception.UnauthorizedException;
import com.truyenchu.demo.repository.FollowRepository;
import com.truyenchu.demo.repository.GenreRepository;
import com.truyenchu.demo.repository.StoryRepository;
import com.truyenchu.demo.repository.TagRepository;
import com.truyenchu.demo.repository.UserRepository;
import com.truyenchu.demo.repository.ChapterUnlockRepository;
import com.truyenchu.demo.repository.ReadingHistoryRepository;
import com.truyenchu.demo.repository.GiftTransactionRepository;
import com.truyenchu.demo.service.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Transactional
public class StoryServiceImpl implements StoryService {
    private static final String CACHE_PREFIX_RECENTLY_UPDATED = "recentlyUpdatedStories:";
    private static final long CACHE_TTL_MINUTES = 30;
    
    private final StoryRepository storyRepository;
    private final FollowRepository followRepository;
    private final GenreRepository genreRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final ChapterUnlockRepository chapterUnlockRepository;
    private final ReadingHistoryRepository readingHistoryRepository;
    private final GiftTransactionRepository giftTransactionRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Story createStory(StoryCreateDTO storyDTO, String username) {
        // Verify user exists
        userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Genre genre = genreRepository.findById(storyDTO.getGenreId())
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found"));

        Set<Tag> tags = new HashSet<>();
        if (storyDTO.getTagIds() != null) {
            tags = new HashSet<>(tagRepository.findAllById(storyDTO.getTagIds()));
        }

        Story story = new Story();
        story.setTitle(storyDTO.getTitle());
        story.setDescription(storyDTO.getDescription());
        story.setAuthor(storyDTO.getAuthor());
        story.setTranslator(userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found")));
        story.setGenre(genre);
        story.setTags(tags);
        story.setStatus(storyDTO.getStatus());
        story.setCoverUrl(storyDTO.getCoverUrl());
        story.setVip(storyDTO.isVip());
        story.setFree(storyDTO.isFree());

        return storyRepository.save(story);
    }

    @Override
    @Transactional
    public Story updateStory(Long id, StoryCreateDTO storyDTO, String username) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!story.getTranslator().equals(user) && !user.getRole().equals(User.UserRole.ADMIN)) {
            throw new UnauthorizedException("Only the translator or admin can update the story");
        }

        Genre genre = genreRepository.findById(storyDTO.getGenreId())
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found"));

        Set<Tag> tags = new HashSet<>();
        if (storyDTO.getTagIds() != null) {
            tags = new HashSet<>(tagRepository.findAllById(storyDTO.getTagIds()));
        }

        story.setTitle(storyDTO.getTitle());
        story.setDescription(storyDTO.getDescription());
        story.setAuthor(storyDTO.getAuthor());
        story.setGenre(genre);
        story.setTags(tags);
        story.setStatus(storyDTO.getStatus());
        story.setCoverUrl(storyDTO.getCoverUrl());
        story.setVip(storyDTO.isVip());
        story.setFree(storyDTO.isFree());

        return storyRepository.save(story);
    }

    @Override
    @Transactional
    public void deleteStory(Long id, String username) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!story.getTranslator().equals(user) && !user.getRole().equals(User.UserRole.ADMIN)) {
            throw new UnauthorizedException("Only the author or admin can delete the story");
        }

        storyRepository.delete(story);
    }

    @Override
    @Transactional(readOnly = true)
    public Story getStoryById(Long id) {
        return storyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Story> getAllStories(Pageable pageable) {
        return storyRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Story> getStoriesByGenre(Long genreId, Pageable pageable) {
        return storyRepository.findByGenreId(genreId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Story> getStoriesByTag(Long tagId, Pageable pageable) {
        return storyRepository.findByTagsId(tagId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Story> searchStories(String keyword, Pageable pageable) {
        return storyRepository.findByTitleContainingOrDescriptionContaining(keyword, keyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Story> findAll(Pageable pageable) {
        return storyRepository.findAll(pageable);
    }

 

    @Override
    @Transactional(readOnly = true)
    public Page<Story> findByTranslator(User translator, Pageable pageable) {
        return storyRepository.findByTranslator(translator, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Story> findByGenre(Long genreId, Pageable pageable) {
        return storyRepository.findByGenreId(genreId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Story> searchByTitle(String keyword, Pageable pageable) {
        return storyRepository.searchByTitle(keyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Story> findTopRatedStories(Pageable pageable) {
        return storyRepository.findTopRatedStories(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Story> findMostFollowedStories(Pageable pageable) {
        return storyRepository.findMostFollowedStories(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Story> findLatestStories(Pageable pageable) {
        return storyRepository.findLatestStories(pageable);
    }

    @Override
    public void followStory(Long storyId, User user) {
        Story story = getStoryById(storyId);
        if (followRepository.findByUserAndStory(user, story).isPresent()) {
            throw new RuntimeException("Already following this story");
        }
        Follow follow = new Follow();
        follow.setUser(user);
        follow.setStory(story);
        followRepository.save(follow);
        long count = followRepository.countByStory(story);
        story.setFollowCount(count);
        storyRepository.save(story);
    }

    @Override
    public void unfollowStory(Long storyId, User user) {
        Story story = getStoryById(storyId);
        followRepository.deleteByUserAndStory(user, story);
        long count = followRepository.countByStory(story);
        story.setFollowCount(count);
        storyRepository.save(story);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(Long storyId, User user) {
        Story story = getStoryById(storyId);
        return followRepository.findByUserAndStory(user, story).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public long getFollowerCount(Long storyId) {
        Story story = getStoryById(storyId);
        return followRepository.countByStory(story);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Story> getRecommendedStories(User user, int limit) {
        // Implementation for story recommendations based on user preferences
        // This would typically involve a recommendation algorithm
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StoryListDTO> getAllStoriesForList(Pageable pageable) {
        return storyRepository.findAllWithDetails(pageable)
                .map(StoryListDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StoryListDTO> getStoriesByGenreForList(Long genreId, Pageable pageable) {
        return storyRepository.findByGenreIdWithDetails(genreId, pageable)
                .map(StoryListDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StoryListDTO> getStoriesByTagForList(Long tagId, Pageable pageable) {
        return storyRepository.findByTagsIdWithDetails(tagId, pageable)
                .map(StoryListDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StoryListDTO> searchStoriesForList(String keyword, Pageable pageable) {
        return storyRepository.searchWithDetails(keyword, pageable)
                .map(StoryListDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public StoryDetailDTO getStoryDetail(Long id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found"));
        
        // Get current user from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            currentUser = userRepository.findByUsername(authentication.getName())
                    .orElse(null);
        }
        
        return StoryDetailDTO.fromEntity(story, currentUser, chapterUnlockRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Story> findByAuthor(String username, Pageable pageable) {
        return storyRepository.findByAuthor(username, pageable);
    }

    @Override
    public List<Story> searchStoriesSimple(String keyword) {
        return storyRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword);
    }

    @Override
    public Page<StoryListDTO> getFreeStories(Pageable pageable) {
        Page<Story> stories = storyRepository.findByIsFreeTrue(pageable);
        return stories.map(StoryListDTO::fromEntity);
    }

    @Override
    public Page<StoryListDTO> getFreeStoriesByGenreName(String genreName, Pageable pageable) {
        Genre genre = genreRepository.findByNameIgnoreCase(genreName)
            .orElseThrow(() -> new ResourceNotFoundException("Genre not found"));
        Page<Story> stories = storyRepository.findByIsFreeTrueAndGenre(genre, pageable);
        return stories.map(StoryListDTO::fromEntity);
    }

    @Override
    public Page<StoryListDTO> getCompletedStoriesByGenreName(String genreName, Pageable pageable) {
        Genre genre = genreRepository.findByNameIgnoreCase(genreName)
        .orElseThrow(() -> new ResourceNotFoundException("Genre not found"));
        Page<Story> stories = storyRepository.findByStatusAndGenre(StoryStatus.COMPLETED, genre, pageable);
        return stories.map(StoryListDTO::fromEntity);
    }

    @Override
    public Page<StoryListDTO> getCompletedStories(Pageable pageable) {
        Page<Story> stories = storyRepository.findByStatus(StoryStatus.COMPLETED, pageable);
        return stories.map(StoryListDTO::fromEntity);
    }

    @Override
    public Page<StoryListDTO> getFeaturedStories(Pageable pageable) {
        Page<Story> stories = storyRepository.findAllByOrderByViewCountDesc(pageable);
        return stories.map(StoryListDTO::fromEntity);
    }

    @Override
    public Page<StoryListDTO> getFeaturedStoriesByGenreName(String genreName, Pageable pageable) {
        Genre genre = genreRepository.findByNameIgnoreCase(genreName)
        .orElseThrow(() -> new ResourceNotFoundException("Genre not found"));
        Page<Story> stories = storyRepository.findByGenreOrderByViewCountDesc(genre, pageable);
        return stories.map(StoryListDTO::fromEntity);
    }

    @Override
    public Page<StoryListDTO> getRecentlyUpdatedStories(Pageable pageable) {
        String cacheKey = CACHE_PREFIX_RECENTLY_UPDATED + pageable.getPageNumber() + "_" + pageable.getPageSize();
        
        // Kiểm tra cache
        Object cachedValue = redisTemplate.opsForValue().get(cacheKey);
        if (cachedValue != null) {
            try {
                // Deserialize với TypeReference để đảm bảo đúng type
                PageResponse<StoryListDTO> cachedResponse = objectMapper.convertValue(
                    cachedValue, 
                    new TypeReference<PageResponse<StoryListDTO>>() {}
                );
                // Convert PageResponse về Page
                return new PageImpl<>(cachedResponse.getContent(), pageable, cachedResponse.getTotalElements());
            } catch (Exception e) {
                // Nếu deserialize fail, xóa cache và query lại từ DB
                redisTemplate.delete(cacheKey);
            }
        }
        
        // Nếu không có trong cache, query từ database
        Page<Story> stories = storyRepository.findAllByOrderByUpdatedAtDesc(pageable);
        Page<StoryListDTO> result = stories.map(StoryListDTO::fromEntity);
        
        // Convert Page sang PageResponse trước khi lưu vào cache
        PageResponse<StoryListDTO> pageResponse = PageResponse.fromPage(result);
        redisTemplate.opsForValue().set(cacheKey, pageResponse, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        
        return result;
    }

    @Override
    public Page<StoryListDTO> getRecentlyUpdatedStoriesByGenre(String genreName, Pageable pageable) {
        String cacheKey = CACHE_PREFIX_RECENTLY_UPDATED + "genre_" + genreName + "_" + pageable.getPageNumber() + "_" + pageable.getPageSize();
        
        // Kiểm tra cache
        Object cachedValue = redisTemplate.opsForValue().get(cacheKey);
        if (cachedValue != null) {
            try {
                // Deserialize với TypeReference để đảm bảo đúng type
                PageResponse<StoryListDTO> cachedResponse = objectMapper.convertValue(
                    cachedValue, 
                    new TypeReference<PageResponse<StoryListDTO>>() {}
                );
                // Convert PageResponse về Page
                return new PageImpl<>(cachedResponse.getContent(), pageable, cachedResponse.getTotalElements());
            } catch (Exception e) {
                // Nếu deserialize fail, xóa cache và query lại từ DB
                redisTemplate.delete(cacheKey);
            }
        }
        
        // Nếu không có trong cache, query từ database
        Genre genre = genreRepository.findByNameIgnoreCase(genreName)
            .orElseThrow(() -> new ResourceNotFoundException("Genre not found"));
        Page<Story> stories = storyRepository.findByGenreOrderByUpdatedAtDesc(genre, pageable);
        Page<StoryListDTO> result = stories.map(StoryListDTO::fromEntity);
        
        // Convert Page sang PageResponse trước khi lưu vào cache
        PageResponse<StoryListDTO> pageResponse = PageResponse.fromPage(result);
        redisTemplate.opsForValue().set(cacheKey, pageResponse, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        
        return result;
    }

    @Override
    public Page<StoryVoteDTO> getMostVotedStories(Pageable pageable) {
        Page<Object[]> results = storyRepository.findAllWithVoteCount(pageable);
        return results.map(result -> {
            Story story = (Story) result[0];
            Long voteCount = ((Number) result[1]).longValue();
            return StoryVoteDTO.fromEntity(story, voteCount);
        });
    }

    @Override
    public Page<StoryVoteDTO> getMostVotedStoriesByGenre(String genreName, Pageable pageable) {
        Genre genre = genreRepository.findByNameIgnoreCase(genreName)
            .orElseThrow(() -> new ResourceNotFoundException("Genre not found"));
        Page<Object[]> results = storyRepository.findByGenreWithVoteCount(genre, pageable);
        return results.map(result -> {
            Story story = (Story) result[0];
            Long voteCount = ((Number) result[1]).longValue();
            return StoryVoteDTO.fromEntity(story, voteCount);
        });
    }

    @Override
    public Page<StoryFollowDTO> getMostFollowedStories(Pageable pageable) {
        Page<Object[]> results = storyRepository.findAllWithFollowerCount(pageable);
        return results.map(result -> {
            Story story = (Story) result[0];
            Long followerCount = ((Number) result[1]).longValue();
            return StoryFollowDTO.fromEntity(story, followerCount);
        });
    }

    @Override
    public Page<StoryFollowDTO> getMostFollowedStoriesByGenre(String genreName, Pageable pageable) {
        Genre genre = genreRepository.findByNameIgnoreCase(genreName)
            .orElseThrow(() -> new ResourceNotFoundException("Genre not found"));
        Page<Object[]> results = storyRepository.findByGenreWithFollowerCount(genre, pageable);
        return results.map(result -> {
            Story story = (Story) result[0];
            Long followerCount = ((Number) result[1]).longValue();
            return StoryFollowDTO.fromEntity(story, followerCount);
        });
    }

    @Override
    public Page<StoryViewDTO> getMostViewedStories(Pageable pageable) {
        Page<Story> stories = storyRepository.findAllByOrderByViewCountDesc(pageable);
        return stories.map(StoryViewDTO::fromEntity);
    }

    @Override
    public Page<StoryViewDTO> getMostViewedStoriesByGenre(String genreName, Pageable pageable) {
        Genre genre = genreRepository.findByNameIgnoreCase(genreName)
            .orElseThrow(() -> new ResourceNotFoundException("Genre not found"));
        Page<Story> stories = storyRepository.findByGenreOrderByViewCountDesc(genre, pageable);
        return stories.map(StoryViewDTO::fromEntity);
    }

    @Override
    public Page<StoryUnlockDTO> getMostUnlockedStories(boolean last24Hours, Pageable pageable) {
        Page<Object[]> results;
        if (last24Hours) {
            LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
            results = storyRepository.findAllWithUnlockCountLast24Hours(twentyFourHoursAgo, pageable);
        } else {
            results = storyRepository.findAllWithUnlockCount(pageable);
        }
        
        return results.map(result -> {
            Story story = (Story) result[0];
            Long unlockCount = ((Number) result[1]).longValue();
            return StoryUnlockDTO.fromEntity(story, unlockCount);
        });
    }

    @Override
    public Page<StoryUnlockDTO> getMostUnlockedStoriesByGenre(String genreName, boolean last24Hours, Pageable pageable) {
        Genre genre = genreRepository.findByNameIgnoreCase(genreName)
            .orElseThrow(() -> new ResourceNotFoundException("Genre not found"));
        
        Page<Object[]> results;
        if (last24Hours) {
            LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
            results = storyRepository.findByGenreWithUnlockCountLast24Hours(genre, twentyFourHoursAgo, pageable);
        } else {
            results = storyRepository.findByGenreWithUnlockCount(genre, pageable);
        }
        
        return results.map(result -> {
            Story story = (Story) result[0];
            Long unlockCount = ((Number) result[1]).longValue();
            return StoryUnlockDTO.fromEntity(story, unlockCount);
        });
    }

    @Override
    public Page<StoryGiftDTO> getMostGiftedStories(Pageable pageable) {
        Page<Object[]> results = storyRepository.findAllWithGiftAmount(pageable);
        return results.map(result -> {
            Story story = (Story) result[0];
            BigDecimal giftAmount = (BigDecimal) result[1];
            return StoryGiftDTO.fromEntity(story, giftAmount);
        });
    }

    @Override
    public Page<StoryGiftDTO> getMostGiftedStoriesByGenre(String genreName, Pageable pageable) {
        Genre genre = genreRepository.findByNameIgnoreCase(genreName)
            .orElseThrow(() -> new ResourceNotFoundException("Genre not found"));
        Page<Object[]> results = storyRepository.findByGenreWithGiftAmount(genre, pageable);
        return results.map(result -> {
            Story story = (Story) result[0];
            BigDecimal giftAmount = (BigDecimal) result[1];
            return StoryGiftDTO.fromEntity(story, giftAmount);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTopTranslatorsByChapterCount(int limit) {
        List<Object[]> results = storyRepository.findTopTranslatorsByChapterCount(limit);
        List<Map<String, Object>> topTranslators = new ArrayList<>();
        
        for (Object[] result : results) {
            User translator = (User) result[0];
            Long chapterCount = ((Number) result[1]).longValue();
            
            Map<String, Object> translatorInfo = new HashMap<>();
            translatorInfo.put("id", translator.getId());
            translatorInfo.put("username", translator.getUsername());
            translatorInfo.put("avatar", translator.getAvatar());
            translatorInfo.put("title", translator.getTitle() != null ? translator.getTitle().getName() : null);
            translatorInfo.put("chapterCount", chapterCount);
            topTranslators.add(translatorInfo);
        }
        
        return topTranslators;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTopUsersByDepositAmount(int limit) {
        List<Object[]> results = storyRepository.findTopUsersByDepositAmount(limit);
        List<Map<String, Object>> topDepositors = new ArrayList<>();
        
        for (Object[] result : results) {
            User user = (User) result[0];
            Long totalAmount = ((Number) result[1]).longValue();
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("avatar", user.getAvatar());
            userInfo.put("title", user.getTitle() != null ? user.getTitle().getName() : null);
            userInfo.put("totalDepositAmount", totalAmount);
            topDepositors.add(userInfo);
        }
        
        return topDepositors;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTopUsersByGiftAmount(int limit) {
        List<Object[]> results = storyRepository.findTopUsersByGiftAmount(limit);
        List<Map<String, Object>> topGifters = new ArrayList<>();
        
        for (Object[] result : results) {
            User user = (User) result[0];
            BigDecimal totalGiftAmount = (BigDecimal) result[1];
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("avatar", user.getAvatar());
            userInfo.put("title", user.getTitle() != null ? user.getTitle().getName() : null);
            userInfo.put("totalGiftAmount", totalGiftAmount);
            topGifters.add(userInfo);
        }
        
        return topGifters;
    }

    @Override
    public Page<StoryListDTO> getVipMostFollowedStories(Pageable pageable) {
        Page<Story> page = storyRepository.findByIsVipTrueOrderByFollowCountDesc(pageable);
        return page.map(StoryListDTO::fromEntity);
    }

    @Override
    public Page<StoryListDTO> getVipLatestStories(Pageable pageable) {
        Page<Story> page = storyRepository.findByIsVipTrueOrderByUpdatedAtDesc(pageable);
        return page.map(StoryListDTO::fromEntity);
    }

    @Override
    public Page<StoryListDTO> filterStories(String genreName, Boolean isVip, Boolean isFree, String status, String chapterRange, List<String> tagNames, String keyword, Pageable pageable) {
        // Convert status string to enum if provided
        StoryStatus statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusEnum = StoryStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore filter
            }
        }
        Page<Story> page = storyRepository.findByFilters(genreName, isVip, isFree, statusEnum, chapterRange, tagNames, keyword, pageable);
        return page.map(StoryListDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public StoryStatsDTO getStoryStats(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekStart = now.minusDays(7);
        LocalDateTime monthStart = now.minusDays(30);

        // Thống kê lượt đọc
        Long totalViews = story.getViewCount();
        Long todayViews = readingHistoryRepository.countByStoryAndLastReadAtAfter(story, todayStart);
        Long thisWeekViews = readingHistoryRepository.countByStoryAndLastReadAtAfter(story, weekStart);
        Long thisMonthViews = readingHistoryRepository.countByStoryAndLastReadAtAfter(story, monthStart);

        // Thống kê lượt đề cử (follow)
        Long totalFollows = story.getFollowCount();
        Long todayFollows = followRepository.countByStoryAndCreatedAtAfter(story, todayStart);
        Long thisWeekFollows = followRepository.countByStoryAndCreatedAtAfter(story, weekStart);
        Long thisMonthFollows = followRepository.countByStoryAndCreatedAtAfter(story, monthStart);

        // Thống kê lượt mở khóa
        Long totalUnlocks = chapterUnlockRepository.countByStory(story);
        Long todayUnlocks = chapterUnlockRepository.countByStoryAndCreatedAtAfter(story, todayStart);
        Long thisWeekUnlocks = chapterUnlockRepository.countByStoryAndCreatedAtAfter(story, weekStart);
        Long thisMonthUnlocks = chapterUnlockRepository.countByStoryAndCreatedAtAfter(story, monthStart);

        // Thống kê tiền mở khóa
        BigDecimal totalUnlockRevenue = chapterUnlockRepository.sumAmountByStory(story);
        BigDecimal todayUnlockRevenue = chapterUnlockRepository.sumAmountByStoryAndCreatedAtAfter(story, todayStart);
        BigDecimal thisWeekUnlockRevenue = chapterUnlockRepository.sumAmountByStoryAndCreatedAtAfter(story, weekStart);
        BigDecimal thisMonthUnlockRevenue = chapterUnlockRepository.sumAmountByStoryAndCreatedAtAfter(story, monthStart);

        // Thống kê tiền quà tặng
        BigDecimal totalGiftRevenue = giftTransactionRepository.sumGiftAmountByStory(story);
        BigDecimal todayGiftRevenue = giftTransactionRepository.sumGiftAmountByStoryAndCreatedAtAfter(story, todayStart);
        BigDecimal thisWeekGiftRevenue = giftTransactionRepository.sumGiftAmountByStoryAndCreatedAtAfter(story, weekStart);
        BigDecimal thisMonthGiftRevenue = giftTransactionRepository.sumGiftAmountByStoryAndCreatedAtAfter(story, monthStart);

        // Tính tổng doanh thu
        BigDecimal totalRevenue = (totalUnlockRevenue != null ? totalUnlockRevenue : BigDecimal.ZERO)
                .add(totalGiftRevenue != null ? totalGiftRevenue : BigDecimal.ZERO);
        BigDecimal todayRevenue = (todayUnlockRevenue != null ? todayUnlockRevenue : BigDecimal.ZERO)
                .add(todayGiftRevenue != null ? todayGiftRevenue : BigDecimal.ZERO);
        BigDecimal thisWeekRevenue = (thisWeekUnlockRevenue != null ? thisWeekUnlockRevenue : BigDecimal.ZERO)
                .add(thisWeekGiftRevenue != null ? thisWeekGiftRevenue : BigDecimal.ZERO);
        BigDecimal thisMonthRevenue = (thisMonthUnlockRevenue != null ? thisMonthUnlockRevenue : BigDecimal.ZERO)
                .add(thisMonthGiftRevenue != null ? thisMonthGiftRevenue : BigDecimal.ZERO);

        return new StoryStatsDTO(
                story.getId(),
                story.getTitle(),
                totalViews,
                todayViews,
                thisWeekViews,
                thisMonthViews,
                totalFollows,
                todayFollows,
                thisWeekFollows,
                thisMonthFollows,
                totalUnlocks,
                todayUnlocks,
                thisWeekUnlocks,
                thisMonthUnlocks,
                totalUnlockRevenue != null ? totalUnlockRevenue : BigDecimal.ZERO,
                todayUnlockRevenue != null ? todayUnlockRevenue : BigDecimal.ZERO,
                thisWeekUnlockRevenue != null ? thisWeekUnlockRevenue : BigDecimal.ZERO,
                thisMonthUnlockRevenue != null ? thisMonthUnlockRevenue : BigDecimal.ZERO,
                totalGiftRevenue != null ? totalGiftRevenue : BigDecimal.ZERO,
                todayGiftRevenue != null ? todayGiftRevenue : BigDecimal.ZERO,
                thisWeekGiftRevenue != null ? thisWeekGiftRevenue : BigDecimal.ZERO,
                thisMonthGiftRevenue != null ? thisMonthGiftRevenue : BigDecimal.ZERO,
                totalRevenue,
                todayRevenue,
                thisWeekRevenue,
                thisMonthRevenue
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StoryStatsDTO> getStoryStatsByTranslator(String username, Pageable pageable) {
        User translator = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Page<Story> stories = storyRepository.findByTranslator(translator, pageable);
        return stories.map(story -> getStoryStats(story.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StoryFanDTO> getStoryFans(Long storyId, Pageable pageable) {
        // Kiểm tra truyện có tồn tại không
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found"));
        
        // Lấy dữ liệu từ 2 query riêng biệt
        List<Object[]> voters = storyRepository.findStoryVotersByStoryId(storyId);
        List<Object[]> gifters = storyRepository.findStoryGiftersByStoryId(storyId);
        
        // Tạo map để kết hợp dữ liệu
        Map<Long, StoryFanDTO> fanMap = new HashMap<>();
        
        // Xử lý dữ liệu voters
        for (Object[] result : voters) {
            User user = (User) result[0];
            Long voteCount = ((Number) result[1]).longValue();
            fanMap.put(user.getId(), StoryFanDTO.fromEntity(user, voteCount, 0L));
        }
        
        // Xử lý dữ liệu gifters
        for (Object[] result : gifters) {
            User user = (User) result[0];
            Long giftCoins = ((Number) result[1]).longValue();
            
            if (fanMap.containsKey(user.getId())) {
                // User đã có trong map, cập nhật giftCoins
                StoryFanDTO existingFan = fanMap.get(user.getId());
                existingFan.setGiftCoins(giftCoins);
                existingFan.setGiftPoints(giftCoins * 100);
                existingFan.setTotalPoints(existingFan.getVotePoints() + existingFan.getGiftPoints());
            } else {
                // User chưa có trong map, tạo mới
                fanMap.put(user.getId(), StoryFanDTO.fromEntity(user, 0L, giftCoins));
            }
        }
        
        // Chuyển đổi map thành list và sắp xếp theo totalPoints
        List<StoryFanDTO> allFans = new ArrayList<>(fanMap.values());
        allFans.sort((a, b) -> Long.compare(b.getTotalPoints(), a.getTotalPoints()));
        
        // Thực hiện phân trang thủ công
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allFans.size());
        
        if (start > allFans.size()) {
            start = allFans.size();
        }
        
        List<StoryFanDTO> pageContent = allFans.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, allFans.size());
    }
} 