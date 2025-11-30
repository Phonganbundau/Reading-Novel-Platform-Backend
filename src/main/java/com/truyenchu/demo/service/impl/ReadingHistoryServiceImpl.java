package com.truyenchu.demo.service.impl;

import com.truyenchu.demo.entity.Chapter;
import com.truyenchu.demo.entity.ReadingHistory;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.repository.ChapterRepository;
import com.truyenchu.demo.repository.ReadingHistoryRepository;
import com.truyenchu.demo.service.ReadingHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReadingHistoryServiceImpl implements ReadingHistoryService {
    private final ReadingHistoryRepository readingHistoryRepository;
    private final ChapterRepository chapterRepository;

    @Override
    public ReadingHistory recordReading(User user, Story story, Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        ReadingHistory history = readingHistoryRepository.findByUserAndStory(user, story)
                .orElse(new ReadingHistory());

        history.setUser(user);
        history.setStory(story);
        history.setChapter(chapter);
        
        return readingHistoryRepository.save(history);
    }

    @Override
    public ReadingHistory updateReadingProgress(Long id, Long chapterId) {
        ReadingHistory history = findById(id);
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        
        history.setChapter(chapter);
        return readingHistoryRepository.save(history);
    }

    @Override
    public void deleteReadingHistory(Long id) {
        readingHistoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ReadingHistory findById(Long id) {
        return readingHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reading history not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public ReadingHistory findByUserAndStory(User user, Story story) {
        return readingHistoryRepository.findByUserAndStory(user, story)
                .orElseThrow(() -> new RuntimeException("Reading history not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReadingHistory> findByUser(User user, Pageable pageable) {
        return readingHistoryRepository.findByUser(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReadingHistory> findRecentReadingHistory(User user, Pageable pageable) {
        return readingHistoryRepository.findRecentReadingHistory(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long countReadersByStory(Story story) {
        return readingHistoryRepository.countReadersByStory(story);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReadingHistory> findRecentReaders(Story story, Pageable pageable) {
        return readingHistoryRepository.findRecentReaders(story, pageable);
    }
} 