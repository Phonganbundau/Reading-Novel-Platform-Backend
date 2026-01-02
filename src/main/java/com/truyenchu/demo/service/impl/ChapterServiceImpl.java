package com.truyenchu.demo.service.impl;

import com.truyenchu.demo.dto.ChapterContentDTO;
import com.truyenchu.demo.dto.ChapterDTO;
import com.truyenchu.demo.dto.ChapterListDTO;
import com.truyenchu.demo.entity.Chapter;
import com.truyenchu.demo.entity.ChapterUnlock;
import com.truyenchu.demo.entity.Follow;
import com.truyenchu.demo.entity.ReadingHistory;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.exception.ResourceNotFoundException;
import com.truyenchu.demo.exception.UnauthorizedException;
import com.truyenchu.demo.repository.ChapterRepository;
import com.truyenchu.demo.repository.ChapterUnlockRepository;
import com.truyenchu.demo.repository.FollowRepository;
import com.truyenchu.demo.repository.ReadingHistoryRepository;
import com.truyenchu.demo.repository.StoryRepository;
import com.truyenchu.demo.repository.UserRepository;
import com.truyenchu.demo.service.ChapterService;
import com.truyenchu.demo.service.NotificationService;
import com.truyenchu.demo.service.NotificationProducer;
import com.truyenchu.demo.dto.NewChapterNotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {
    private final ChapterRepository chapterRepository;
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final ChapterUnlockRepository chapterUnlockRepository;
    private final ReadingHistoryRepository readingHistoryRepository;
    private final NotificationService notificationService;
    private final NotificationProducer notificationProducer;
    private final FollowRepository followRepository;

    @Override
    public ChapterDTO createChapter(ChapterDTO chapterDTO, User user) {
        Story story = storyRepository.findById(chapterDTO.getStoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Story not found"));

        // Kiểm tra quyền tạo chapter
        if (!user.getRole().equals(User.UserRole.ADMIN) && !story.getTranslator().equals(user)) {
            throw new UnauthorizedException("You don't have permission to create chapters for this story");
        }

        Chapter chapter = new Chapter();
        chapter.setStory(story);
        chapter.setTitle(chapterDTO.getTitle());
        chapter.setContent(chapterDTO.getContent());
        chapter.setChapterNumber(chapterDTO.getChapterNumber());
        chapter.setLocked(chapterDTO.isLocked());
        chapter.setPrice(chapterDTO.getPrice());
        
        story.setChapterCount(story.getChapterCount() + 1);
        story.setUpdatedAt(LocalDateTime.now());
        storyRepository.save(story);

        Chapter savedChapter = chapterRepository.save(chapter);


        
        
        List<Follow> storyFollowers = followRepository.findByStory(story);
       
        
        for (Follow follow : storyFollowers) {
            User follower = follow.getUser();
         
            
            // Không gửi thông báo cho chính người tạo chapter
            if (!follower.equals(user)) {
      
                // Tạo message và gửi vào RabbitMQ queue để xử lý async
                NewChapterNotificationMessage message = new NewChapterNotificationMessage(
                    story.getId(),
                    story.getTitle(),
                    savedChapter.getChapterNumber(),
                    savedChapter.getTitle(),
                    user.getId(),
                    follower.getId()
                );
                notificationProducer.sendNewChapterNotification(message);
            } else {
                
            }
        }
        


        return ChapterDTO.fromEntity(savedChapter);
    }

    @Override
    public ChapterDTO updateChapter(Long id, ChapterDTO chapterDTO, User user) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));

        // Kiểm tra quyền cập nhật chapter
        if (!user.getRole().equals(User.UserRole.ADMIN) && !chapter.getStory().getTranslator().equals(user)) {
            throw new UnauthorizedException("You don't have permission to update this chapter");
        }

        chapter.setTitle(chapterDTO.getTitle());
        chapter.setContent(chapterDTO.getContent());
        chapter.setChapterNumber(chapterDTO.getChapterNumber());
        chapter.setLocked(chapterDTO.isLocked());
        if (chapter.getPrice() == null) {
            chapter.setPrice(BigDecimal.ZERO); 
        }
        chapter.setPrice(chapterDTO.getPrice());

        Chapter updatedChapter = chapterRepository.save(chapter);
        return ChapterDTO.fromEntity(updatedChapter);
    }

    @Override
    public void deleteChapter(Long id, User user) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));

        // Kiểm tra quyền xóa chapter
        if (!user.getRole().equals(User.UserRole.ADMIN) && !chapter.getStory().getTranslator().equals(user)) {
            throw new UnauthorizedException("You don't have permission to delete this chapter");
        }

        Story story = chapter.getStory();

        story.setChapterCount(story.getChapterCount() - 1);
        storyRepository.save(story);

        chapterRepository.delete(chapter);
    }

    @Override
    public ChapterDTO getChapter(Long id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));
        return ChapterDTO.fromEntity(chapter);
    }

    @Override
    public ChapterDTO getChapterByStoryAndNumber(Long storyId, Integer chapterNumber) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found"));
        
        Chapter chapter = chapterRepository.findByStoryAndChapterNumber(story, chapterNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));
        
        return ChapterDTO.fromEntity(chapter);
    }

    @Override
    public Page<ChapterDTO> getChaptersByStory(Long storyId, Pageable pageable) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found"));
        
        return chapterRepository.findByStory(story, pageable)
                .map(ChapterDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public ChapterContentDTO getChapterContent(Long id, User currentUser) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));

        // Kiểm tra quyền đọc chapter
        boolean isUnlocked = false;
        if (currentUser != null) {
            // Admin hoặc tác giả có thể đọc tất cả chapter
            if (currentUser.getRole().equals(User.UserRole.ADMIN) || 
                chapter.getStory().getAuthor().equals(currentUser.getUsername())) {
                isUnlocked = true;
            } else {
                // Kiểm tra xem user đã mở khóa chapter chưa
                isUnlocked = chapterUnlockRepository.existsByUserAndChapter(currentUser, chapter);
            }
        }

        // Nếu chapter có phí và chưa mở khóa, throw exception
        if (chapter.isLocked() && !isUnlocked) {
            throw new UnauthorizedException("Chapter is locked. Please purchase to read.");
        }

        ReadingHistory readingHistory = new ReadingHistory();
        readingHistory.setUser(currentUser);
        readingHistory.setChapter(chapter);
        readingHistory.setStory(chapter.getStory());
        readingHistory.setLastReadAt(LocalDateTime.now());
        readingHistoryRepository.save(readingHistory);

        return ChapterContentDTO.fromEntity(chapter, isUnlocked);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterDTO> getNextChapters(Long storyId, Integer currentChapter, int limit) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found"));
        
        List<Chapter> chapters = chapterRepository.findNextChapters(story, currentChapter, limit);
        return chapters.stream()
                .map(ChapterDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterDTO> getPreviousChapters(Long storyId, Integer currentChapter, int limit) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found"));
        
        List<Chapter> chapters = chapterRepository.findPreviousChapters(story, currentChapter, limit);
        return chapters.stream()
                .map(ChapterDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isChapterUnlocked(Long chapterId, String username) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));

   

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return canAccessChapter(user, chapter);
    }

    @Override
    @Transactional
    public void unlockChapter(Long id, String username) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!chapter.isLocked()) {
            return;
        }

       ChapterUnlock chapterUnlock = new ChapterUnlock();
       chapterUnlock.setUser(user);
       chapterUnlock.setChapter(chapter);
    


        if (user.getCoinBalance().compareTo(chapter.getPrice()) < 0) {
            throw new UnauthorizedException("Insufficient balance to unlock chapter");
        }
        
        user.setCoinBalance(user.getCoinBalance().subtract(chapter.getPrice()));
        userRepository.save(user);
        chapterUnlockRepository.save(chapterUnlock);
        Story story = chapter.getStory();
        User translator = story.getTranslator();
        if (translator != null) {
            translator.setCoinEarning(translator.getCoinEarning().add(chapter.getPrice()));
            userRepository.save(translator);
        }
    }

    @Override
    public boolean canUserAccessChapter(Long chapterId, User user) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));

        if (!chapter.isLocked()) {
            return true;
        }

        if (user.getRole().equals(User.UserRole.ADMIN) || chapter.getStory().getTranslator().getUsername().equals(user.getUsername())) {
            return true;
        }

        // Check if user has purchased the chapter
        return chapterRepository.existsByStoryAndChapterNumber(chapter.getStory(), chapter.getChapterNumber());
    }

    @Override
    public boolean isChapterLocked(Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));
        return chapter.isLocked();
    }

    @Override
    @Transactional
    public void purchaseChapter(Long chapterId, User user) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));

        if (!chapter.isLocked()) {
            return;
        }

        if (user.getCoinBalance().compareTo(chapter.getPrice()) < 0) {
            throw new UnauthorizedException("Insufficient balance to purchase chapter");
        }

        user.setCoinBalance(user.getCoinBalance().subtract(chapter.getPrice()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void unlockChapter(Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));
        chapter.setLocked(false);
        chapterRepository.save(chapter);
    }

    @Override
    @Transactional
    public void lockChapter(Long chapterId, Integer price) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));
        chapter.setLocked(true);
        chapter.setPrice(new BigDecimal(price));
        chapterRepository.save(chapter);
    }

    @Override
    public long countLockedChapters(Story story) {
        return chapterRepository.countByStoryAndLockedTrue(story);
    }

    @Override
    public boolean existsByStoryAndChapterNumber(Story story, Integer chapterNumber) {
        return chapterRepository.existsByStoryAndChapterNumber(story, chapterNumber);
    }

    @Override
    public boolean canAccessChapter(User user, Chapter chapter) {
        if (chapter == null) return false;
        
        // Chương có bị khóa không? Nếu không thì trả về true
        if (chapter.isLocked() == false) {
            return true;
        }
        
        // Kiểm tra nếu user là dịch giả hoặc admin
        if (user != null && (user.equals(chapter.getStory().getTranslator()) || 
            user.getRole().equals(User.UserRole.ADMIN))) {
            return true;
        }
        
        // Kiểm tra nếu chương đã được mở khóa
        if (user != null) {
            return chapterUnlockRepository.findByUserAndChapter(user, chapter).isPresent();
        }
        
        return false;
    }

    @Override
    public ChapterDTO getNextChapter(Long storyId, Long chapterId) {
        Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new ResourceNotFoundException("Story not found"));
        
        Chapter currentChapter = chapterRepository.findById(chapterId)
            .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));

        // Check if chapter belongs to story
        if (!currentChapter.getStory().getId().equals(storyId)) {
            throw new ResourceNotFoundException("Chapter not found in this story");
        }

        Chapter nextChapter = chapterRepository.findByStoryAndChapterNumberGreaterThanOrderByChapterNumberAsc(
            story, currentChapter.getChapterNumber())
            .stream()
            .findFirst()
            .orElse(null);

        return nextChapter != null ? ChapterDTO.fromEntity(nextChapter) : null;
    }

    @Override
    public ChapterDTO getPreviousChapter(Long storyId, Long chapterId) {
        Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new ResourceNotFoundException("Story not found"));
        
        Chapter currentChapter = chapterRepository.findById(chapterId)
            .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));

        // Check if chapter belongs to story
        if (!currentChapter.getStory().getId().equals(storyId)) {
            throw new ResourceNotFoundException("Chapter not found in this story");
        }

        Chapter previousChapter = chapterRepository.findByStoryAndChapterNumberLessThanOrderByChapterNumberDesc(
            story, currentChapter.getChapterNumber())
            .stream()
            .findFirst()
            .orElse(null);

        return previousChapter != null ? ChapterDTO.fromEntity(previousChapter) : null;
    }

    @Override
    public Page<ChapterListDTO> getLatestChapters(Pageable pageable) {
        Page<Chapter> chapters = chapterRepository.findAll(PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by("createdAt").descending()
        ));

        return chapters.map(ChapterListDTO::fromEntity);
    }

}