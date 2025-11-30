package com.truyenchu.demo.controller;

import com.truyenchu.demo.dto.ChapterContentDTO;
import com.truyenchu.demo.dto.ChapterDTO;
import com.truyenchu.demo.dto.ChapterListDTO;
import com.truyenchu.demo.entity.Chapter;
import com.truyenchu.demo.entity.ReadingHistory;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.exception.ResourceNotFoundException;
import com.truyenchu.demo.exception.UnauthorizedException;
import com.truyenchu.demo.repository.ChapterRepository;
import com.truyenchu.demo.repository.ReadingHistoryRepository;
import com.truyenchu.demo.repository.StoryRepository;
import com.truyenchu.demo.repository.UserRepository;
import com.truyenchu.demo.service.ChapterService;
import com.truyenchu.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
public class ChapterController {
    private final ChapterService chapterService;
    private final UserService userService;
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final UserRepository userRepository;
    private final ReadingHistoryRepository readingHistoryRepository;

    @PostMapping
    public ResponseEntity<ChapterDTO> createChapter(
            @Valid @RequestBody ChapterDTO chapterDTO,
            Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        return ResponseEntity.ok(chapterService.createChapter(chapterDTO, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChapterDTO> updateChapter(
            @PathVariable Long id,
            @Valid @RequestBody ChapterDTO chapterDTO,
            Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        return ResponseEntity.ok(chapterService.updateChapter(id, chapterDTO, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChapter(
            @PathVariable Long id,
            Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        chapterService.deleteChapter(id, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/story/{storyId}/number/{chapterNumber}")
    public ResponseEntity<ChapterDTO> getChapterByStoryAndNumber(
            @PathVariable Long storyId,
            @PathVariable Integer chapterNumber) {
        
        Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new ResourceNotFoundException("Story not found"));
        
        Chapter chapter = chapterRepository.findByStoryAndChapterNumber(story, chapterNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));

        // Get current user from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated()) {
            currentUser = userRepository.findByUsername(authentication.getName())
                .orElse(null);
        }

        // Check if user has access to chapter
        if (!chapterService.canAccessChapter(currentUser, chapter)) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đây là chương vip, bạn cần mua để đọc");
            response.put("price", chapter.getPrice());
            response.put("chapterNumber", chapter.getChapterNumber());
            response.put("totalChapters", story.getChapterCount());
            response.put("title", chapter.getTitle());
            throw new UnauthorizedException(response);
        }

        // Only save reading history and increment count if user is logged in
        if (currentUser != null) {
            // Tìm xem đã có bản ghi nào với user + story chưa
            Optional<ReadingHistory> existingHistoryOpt =
            readingHistoryRepository.findByUserAndStory(currentUser, story);

            ReadingHistory readingHistory;

            if (existingHistoryOpt.isPresent()) {
            // Ghi đè: cập nhật chapter mới và thời gian mới
            readingHistory = existingHistoryOpt.get();
            readingHistory.setChapter(chapter);
            readingHistory.setLastReadAt(LocalDateTime.now());
            } else {
            // Chưa từng đọc: tạo mới
            readingHistory = new ReadingHistory();
            readingHistory.setUser(currentUser);
            readingHistory.setChapter(chapter);
            readingHistory.setStory(story);
            readingHistory.setLastReadAt(LocalDateTime.now());
            }
            readingHistoryRepository.save(readingHistory);

            // Increment chapters read count
            try {
                userService.incrementChaptersRead(currentUser.getUsername());
            } catch (Exception e) {
                // Log error but don't fail the request
                System.err.println("Error incrementing chapters read: " + e.getMessage());
                e.printStackTrace();
            }
        }

        story.setViewCount(story.getViewCount() + 1);
        storyRepository.save(story);

        return ResponseEntity.ok(ChapterDTO.fromEntity(chapter));
    }



    @GetMapping("/story/{storyId}")
    public ResponseEntity<Page<ChapterDTO>> getChaptersByStory(
            @PathVariable Long storyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(chapterService.getChaptersByStory(storyId, PageRequest.of(page, size)));
    }


    @GetMapping("/latest")
    public ResponseEntity<Page<ChapterListDTO>> getLatestChapters(Pageable pageable) {
        Page<ChapterListDTO> chapters = chapterService.getLatestChapters(pageable);
        return ResponseEntity.ok(chapters);
    }

    

    @GetMapping("/story/{storyId}/chapters/{chapterId}/next")
    public ResponseEntity<ChapterDTO> getNextChapter(
            @PathVariable Long storyId,
            @PathVariable Long chapterId) {
        ChapterDTO nextChapter = chapterService.getNextChapter(storyId, chapterId);
        if (nextChapter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(nextChapter);
    }

    @GetMapping("/story/{storyId}/chapters/{chapterId}/previous")
    public ResponseEntity<ChapterDTO> getPreviousChapter(
            @PathVariable Long storyId,
            @PathVariable Long chapterId) {
        ChapterDTO previousChapter = chapterService.getPreviousChapter(storyId, chapterId);
        if (previousChapter == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(previousChapter);
    }

    @GetMapping("/{id}/unlock-status")
    public ResponseEntity<Boolean> isChapterUnlocked(
            @PathVariable Long id,
            Authentication authentication) {
        boolean isUnlocked = chapterService.isChapterUnlocked(id, authentication.getName());
        return ResponseEntity.ok(isUnlocked);
    }

    @PostMapping("/{id}/unlock")
    public ResponseEntity<Void> unlockChapter(
            @PathVariable Long id,
            Authentication authentication) {
        chapterService.unlockChapter(id, authentication.getName());
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<ChapterContentDTO> getChapterContent(
            @PathVariable Long id,
            Authentication authentication) {
        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated()) {
            currentUser = userService.findByUsername(authentication.getName());
        }
        return ResponseEntity.ok(chapterService.getChapterContent(id, currentUser));
    }

    @PostMapping("/buy")
    public ResponseEntity<Void> buyChapter(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        Long storyId = Long.valueOf(request.get("storyId").toString());
        Integer chapterNumber = Integer.valueOf(request.get("chapterNumber").toString());
        
        User user = userService.findByUsername(authentication.getName());
        Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new ResourceNotFoundException("Story not found"));
        
        Chapter chapter = chapterRepository.findByStoryAndChapterNumber(story, chapterNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));

        // Kiểm tra xem đã mua chương này chưa
        if (chapterService.isChapterUnlocked(chapter.getId(), user.getUsername())) {
            throw new UnauthorizedException("Chương này đã có thể truy cập, bạn không cần mua nữa");
        }

        // Kiểm tra số dư
        if (user.getCoinBalance().compareTo(chapter.getPrice()) < 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Số dư không đủ để mua chương này");
            response.put("price", chapter.getPrice());
            response.put("balance", user.getCoinBalance());
            throw new UnauthorizedException(response);
        }

        // Mua chương
        chapterService.unlockChapter(chapter.getId(), user.getUsername());
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChapterDTO> getChapterDetail(@PathVariable Long id) {
        Chapter chapter = chapterRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));
        return ResponseEntity.ok(ChapterDTO.fromEntity(chapter));
    }
} 