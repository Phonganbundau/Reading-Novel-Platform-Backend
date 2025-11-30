package com.truyenchu.demo.repository;

import com.truyenchu.demo.entity.ReadingHistory;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, Long> {
    Optional<ReadingHistory> findByUserAndStory(User user, Story story);
    
    Page<ReadingHistory> findByUser(User user, Pageable pageable);
    
    @Query("SELECT rh FROM ReadingHistory rh WHERE rh.user = :user ORDER BY rh.lastReadAt DESC")
    Page<ReadingHistory> findRecentReadingHistory(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT COUNT(rh) FROM ReadingHistory rh WHERE rh.story = :story")
    long countReadersByStory(@Param("story") Story story);
    
    @Query("SELECT rh FROM ReadingHistory rh WHERE rh.story = :story ORDER BY rh.lastReadAt DESC")
    Page<ReadingHistory> findRecentReaders(@Param("story") Story story, Pageable pageable);

    List<ReadingHistory> findByUserOrderByLastReadAtDesc(User user);
    
    // Thống kê theo thời gian
    long countByStoryAndLastReadAtAfter(Story story, LocalDateTime dateTime);
} 