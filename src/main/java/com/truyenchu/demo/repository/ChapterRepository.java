package com.truyenchu.demo.repository;

import com.truyenchu.demo.entity.Chapter;
import com.truyenchu.demo.entity.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    Optional<Chapter> findByStoryAndChapterNumber(Story story, Integer chapterNumber);
    
    Page<Chapter> findByStory(Story story, Pageable pageable);
    
    @Query("SELECT c FROM Chapter c WHERE c.story = :story AND c.chapterNumber > :currentChapter ORDER BY c.chapterNumber ASC")
    List<Chapter> findNextChapters(@Param("story") Story story, @Param("currentChapter") Integer currentChapter, int limit);
    
    @Query("SELECT c FROM Chapter c WHERE c.story = :story AND c.chapterNumber < :currentChapter ORDER BY c.chapterNumber DESC")
    List<Chapter> findPreviousChapters(@Param("story") Story story, @Param("currentChapter") Integer currentChapter, int limit);
    
    boolean existsByStoryAndChapterNumber(Story story, Integer chapterNumber);
    
    long countByStoryAndLockedTrue(Story story);

    Optional<Chapter> findFirstByStoryOrderByChapterNumberDesc(Story story);

    List<Chapter> findByStoryAndChapterNumberGreaterThanOrderByChapterNumberAsc(Story story, Integer chapterNumber);
    List<Chapter> findByStoryAndChapterNumberLessThanOrderByChapterNumberDesc(Story story, Integer chapterNumber);
} 