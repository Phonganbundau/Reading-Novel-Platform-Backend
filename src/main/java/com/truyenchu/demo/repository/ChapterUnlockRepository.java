package com.truyenchu.demo.repository;

import com.truyenchu.demo.entity.Chapter;
import com.truyenchu.demo.entity.ChapterUnlock;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterUnlockRepository extends JpaRepository<ChapterUnlock, Long> {
    List<ChapterUnlock> findByUserAndChapter_Story(User user, Story story);
    boolean existsByUserAndChapter(User user, Chapter chapter);
    List<ChapterUnlock> findByUser(User user);
    int countByUser(User user);
    Optional<ChapterUnlock> findByUserAndChapter(User user, Chapter chapter);
    
    // Thống kê theo thời gian
    @Query("SELECT COUNT(cu) FROM ChapterUnlock cu WHERE cu.chapter.story = :story")
    long countByStory(@Param("story") Story story);

    @Query("SELECT COUNT(cu) FROM ChapterUnlock cu WHERE cu.chapter.story = :story AND cu.unlockedAt >= :dateTime")
    long countByStoryAndCreatedAtAfter(@Param("story") Story story, @Param("dateTime") LocalDateTime dateTime);

    @Query("SELECT COALESCE(SUM(cu.chapter.price), 0) FROM ChapterUnlock cu WHERE cu.chapter.story = :story")
    BigDecimal sumAmountByStory(@Param("story") Story story);

    @Query("SELECT COALESCE(SUM(cu.chapter.price), 0) FROM ChapterUnlock cu WHERE cu.chapter.story = :story AND cu.unlockedAt >= :dateTime")
    BigDecimal sumAmountByStoryAndCreatedAtAfter(@Param("story") Story story, @Param("dateTime") LocalDateTime dateTime);
} 