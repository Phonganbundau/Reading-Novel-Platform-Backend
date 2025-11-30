package com.truyenchu.demo.service;

import com.truyenchu.demo.dto.ChapterContentDTO;
import com.truyenchu.demo.dto.ChapterDTO;
import com.truyenchu.demo.dto.ChapterListDTO;
import com.truyenchu.demo.entity.Chapter;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChapterService {
    ChapterDTO createChapter(ChapterDTO chapterDTO, User user);
    
    ChapterDTO updateChapter(Long id, ChapterDTO chapterDTO, User user);
    
    void deleteChapter(Long id, User user);
    
    ChapterDTO getChapter(Long id);
    
    Page<ChapterDTO> getChaptersByStory(Long storyId, Pageable pageable);

    Page<ChapterListDTO> getLatestChapters(Pageable pageable);
    
    ChapterDTO getChapterByStoryAndNumber(Long storyId, Integer chapterNumber);
    
    ChapterContentDTO getChapterContent(Long id, User currentUser);
    
    List<ChapterDTO> getNextChapters(Long storyId, Integer currentChapter, int limit);
    
    List<ChapterDTO> getPreviousChapters(Long storyId, Integer currentChapter, int limit);
    
    boolean isChapterUnlocked(Long chapterId, String username);
    
    void unlockChapter(Long chapterId, String username);
    
    boolean existsByStoryAndChapterNumber(Story story, Integer chapterNumber);

    void lockChapter(Long chapterId, Integer coinPrice);
    void unlockChapter(Long chapterId);
    boolean isChapterLocked(Long chapterId);
    boolean canUserAccessChapter(Long chapterId, User user);
    void purchaseChapter(Long chapterId, User user);
    long countLockedChapters(Story story);

    boolean canAccessChapter(User user, Chapter chapter);

    ChapterDTO getNextChapter(Long storyId, Long chapterId);
    ChapterDTO getPreviousChapter(Long storyId, Long chapterId);
} 