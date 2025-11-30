package com.truyenchu.demo.service;

import com.truyenchu.demo.entity.ReadingHistory;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReadingHistoryService {
    ReadingHistory recordReading(User user, Story story, Long chapterId);
    ReadingHistory updateReadingProgress(Long id, Long chapterId);
    void deleteReadingHistory(Long id);
    ReadingHistory findById(Long id);
    ReadingHistory findByUserAndStory(User user, Story story);
    Page<ReadingHistory> findByUser(User user, Pageable pageable);
    Page<ReadingHistory> findRecentReadingHistory(User user, Pageable pageable);
    long countReadersByStory(Story story);
    Page<ReadingHistory> findRecentReaders(Story story, Pageable pageable);
} 