package com.truyenchu.demo.repository;

import com.truyenchu.demo.entity.Genre;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.Story.StoryStatus;
import com.truyenchu.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
    @Query("SELECT DISTINCT s FROM Story s " +
           "LEFT JOIN FETCH s.genre " +
           "LEFT JOIN FETCH s.tags " +
           "LEFT JOIN FETCH s.translator")
    Page<Story> findAllWithDetails(Pageable pageable);
    
    @Query("SELECT DISTINCT s FROM Story s " +
           "LEFT JOIN FETCH s.genre " +
           "LEFT JOIN FETCH s.tags " +
           "LEFT JOIN FETCH s.translator " +
           "WHERE s.author = :author")
    Page<Story> findByAuthorWithDetails(@Param("author") User author, Pageable pageable);
    
    @Query("SELECT DISTINCT s FROM Story s " +
           "LEFT JOIN FETCH s.genre g " +
           "LEFT JOIN FETCH s.tags " +
           "LEFT JOIN FETCH s.translator " +
           "WHERE g.id = :genreId")
    Page<Story> findByGenreIdWithDetails(@Param("genreId") Long genreId, Pageable pageable);
    
    @Query("SELECT DISTINCT s FROM Story s " +
           "LEFT JOIN FETCH s.genre " +
           "LEFT JOIN FETCH s.tags t " +
           "LEFT JOIN FETCH s.translator " +
           "WHERE t.id = :tagId")
    Page<Story> findByTagsIdWithDetails(@Param("tagId") Long tagId, Pageable pageable);
    
    @Query("SELECT DISTINCT s FROM Story s " +
           "LEFT JOIN FETCH s.genre " +
           "LEFT JOIN FETCH s.tags " +
           "LEFT JOIN FETCH s.translator " +
           "WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Story> searchWithDetails(@Param("keyword") String keyword, Pageable pageable);

   
  
    
    @Query("SELECT s FROM Story s JOIN s.genre g WHERE g.id = :genreId")
    Page<Story> findByGenreId(@Param("genreId") Long genreId, Pageable pageable);
    
    @Query("SELECT s FROM Story s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Story> searchByTitle(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT s FROM Story s WHERE s.status = 'PUBLISHED' ORDER BY " +
           "(SELECT COUNT(r) FROM Rating r WHERE r.story = s) DESC")
    Page<Story> findTopRatedStories(Pageable pageable);
    
    @Query("SELECT s FROM Story s ORDER BY " +
           "(SELECT COUNT(f) FROM Follow f WHERE f.story = s) DESC")
    Page<Story> findMostFollowedStories(Pageable pageable);
    
    @Query("SELECT s FROM Story s ORDER BY s.createdAt DESC")
    Page<Story> findLatestStories(Pageable pageable);
    
    @Query("SELECT s FROM Story s WHERE s.status = 'PUBLISHED' AND s.author = :author")
    Page<Story> findPublishedStoriesByAuthor(@Param("author") User author, Pageable pageable);
    
    @Query("SELECT COUNT(s) FROM Story s WHERE s.status = 'PUBLISHED'")
    long countPublishedStories();
    
    @Query("SELECT s FROM Story s JOIN s.tags t WHERE t.id = :tagId")
    Page<Story> findByTagsId(Long tagId, Pageable pageable);
    
    Page<Story> findByTitleContainingOrDescriptionContaining(String title, String description, Pageable pageable);

    Page<Story> findByTranslator(User translator, Pageable pageable);

    Page<Story> findByAuthor(String username, Pageable pageable);

    List<Story> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);

    Page<Story> findByIsFreeTrue(Pageable pageable);
    Page<Story> findByIsFreeTrueAndGenre(Genre genre, Pageable pageable);

    Page<Story> findByStatusAndGenre(StoryStatus  status, Genre genre, Pageable pageable);

    Page<Story> findByStatus(StoryStatus  status, Pageable pageable);

    @Query("SELECT s FROM Story s ORDER BY s.viewCount DESC")
    Page<Story> findAllByOrderByViewCountDesc(Pageable pageable);

    @Query("SELECT s FROM Story s WHERE s.genre = :genre ORDER BY s.viewCount DESC")
    Page<Story> findByGenreOrderByViewCountDesc(@Param("genre") Genre genre, Pageable pageable);

    Page<Story> findAllByOrderByLikeCountDesc(Pageable pageable);
    
    Page<Story> findAllByOrderByUpdatedAtDesc(Pageable pageable);
    Page<Story> findByGenreOrderByUpdatedAtDesc(Genre genre, Pageable pageable);

    @Query("SELECT s, COALESCE(SUM(sv.quantity), 0) as voteCount FROM Story s " +
           "LEFT JOIN StoryVote sv ON s.id = sv.story.id " +
           "GROUP BY s.id ORDER BY voteCount DESC")
    Page<Object[]> findAllWithVoteCount(Pageable pageable);

    @Query("SELECT s, COALESCE(SUM(sv.quantity), 0) as voteCount FROM Story s " +
           "LEFT JOIN StoryVote sv ON s.id = sv.story.id " +
           "WHERE s.genre = :genre " +
           "GROUP BY s.id ORDER BY voteCount DESC")
    Page<Object[]> findByGenreWithVoteCount(@Param("genre") Genre genre, Pageable pageable);

    @Query("SELECT s, COUNT(f.id) as followerCount FROM Story s " +
           "LEFT JOIN Follow f ON s.id = f.story.id " +
           "GROUP BY s.id ORDER BY followerCount DESC")
    Page<Object[]> findAllWithFollowerCount(Pageable pageable);

    @Query("SELECT s, COUNT(f.id) as followerCount FROM Story s " +
           "LEFT JOIN Follow f ON s.id = f.story.id " +
           "WHERE s.genre = :genre " +
           "GROUP BY s.id ORDER BY followerCount DESC")
    Page<Object[]> findByGenreWithFollowerCount(@Param("genre") Genre genre, Pageable pageable);

    @Query("SELECT s, COUNT(cu.id) as unlockCount FROM Story s " +
           "LEFT JOIN s.chapters c " +
           "LEFT JOIN ChapterUnlock cu ON c.id = cu.chapter.id " +
           "GROUP BY s.id ORDER BY unlockCount DESC")
    Page<Object[]> findAllWithUnlockCount(Pageable pageable);

    @Query("SELECT s, COUNT(cu.id) as unlockCount FROM Story s " +
           "LEFT JOIN s.chapters c " +
           "LEFT JOIN ChapterUnlock cu ON c.id = cu.chapter.id " +
           "WHERE s.genre = :genre " +
           "GROUP BY s.id ORDER BY unlockCount DESC")
    Page<Object[]> findByGenreWithUnlockCount(@Param("genre") Genre genre, Pageable pageable);

    @Query("SELECT s, COUNT(cu.id) as unlockCount FROM Story s " +
           "INNER JOIN s.chapters c " +
           "INNER JOIN ChapterUnlock cu ON c.id = cu.chapter.id " +
           "WHERE cu.unlockedAt >= :since " +
           "GROUP BY s.id ORDER BY unlockCount DESC")
    Page<Object[]> findAllWithUnlockCountLast24Hours(@Param("since") LocalDateTime since, Pageable pageable);

    @Query("SELECT s, COUNT(cu.id) as unlockCount FROM Story s " +
           "INNER JOIN s.chapters c " +
           "INNER JOIN ChapterUnlock cu ON c.id = cu.chapter.id " +
           "WHERE s.genre = :genre AND cu.unlockedAt >= :since " +
           "GROUP BY s.id ORDER BY unlockCount DESC")
    Page<Object[]> findByGenreWithUnlockCountLast24Hours(@Param("genre") Genre genre, @Param("since") LocalDateTime since, Pageable pageable);

    @Query("SELECT s, COALESCE(SUM(g.coinCosts), 0) as giftAmount FROM Story s " +
           "LEFT JOIN GiftTransaction gt ON s.id = gt.story.id " +
           "LEFT JOIN Gift g ON gt.gift.id = g.id " +
           "GROUP BY s.id ORDER BY giftAmount DESC")
    Page<Object[]> findAllWithGiftAmount(Pageable pageable);

    @Query("SELECT s, COALESCE(SUM(g.coinCosts), 0) as giftAmount FROM Story s " +
           "LEFT JOIN GiftTransaction gt ON s.id = gt.story.id " +
           "LEFT JOIN Gift g ON gt.gift.id = g.id " +
           "WHERE s.genre = :genre " +
           "GROUP BY s.id ORDER BY giftAmount DESC")
    Page<Object[]> findByGenreWithGiftAmount(@Param("genre") Genre genre, Pageable pageable);

    // Top ranking queries
    @Query("SELECT s.translator, COUNT(c.id) as chapterCount FROM Story s " +
           "LEFT JOIN s.chapters c " +
           "WHERE s.translator IS NOT NULL " +
           "GROUP BY s.translator " +
           "ORDER BY chapterCount DESC")
    List<Object[]> findTopTranslatorsByChapterCount(int limit);

    @Query("""
       SELECT t.user, SUM(t.coins)
       FROM Order t
       WHERE t.status = 'SUCCESS'
       GROUP BY t.user
       ORDER BY SUM(t.coins) DESC
    """)
    List<Object[]> findTopUsersByDepositAmount(int limit);
    
   

    @Query("SELECT gt.sender, SUM(g.coinCosts) as totalGiftAmount FROM GiftTransaction gt " +
           "LEFT JOIN Gift g ON gt.gift.id = g.id " +
           "GROUP BY gt.sender " +
           "ORDER BY totalGiftAmount DESC")
    List<Object[]> findTopUsersByGiftAmount(int limit);

    @Query("SELECT sv.user, SUM(sv.quantity) as voteCount FROM StoryVote sv " +
           "WHERE sv.story.id = :storyId " +
           "GROUP BY sv.user")
    List<Object[]> findStoryVotersByStoryId(@Param("storyId") Long storyId);

    @Query("SELECT gt.sender, SUM(g.coinCosts) as giftCoins FROM GiftTransaction gt " +
           "LEFT JOIN Gift g ON gt.gift.id = g.id " +
           "WHERE gt.story.id = :storyId " +
           "GROUP BY gt.sender")
    List<Object[]> findStoryGiftersByStoryId(@Param("storyId") Long storyId);


    Page<Story> findByIsVipTrueOrderByFollowCountDesc(Pageable pageable);

    Page<Story> findByIsVipTrueOrderByUpdatedAtDesc(Pageable pageable);

    @Query("SELECT DISTINCT s FROM Story s LEFT JOIN s.tags t WHERE " +
           "(:genreName IS NULL OR LOWER(s.genre.name) = LOWER(:genreName)) AND " +
           "(:isVip IS NULL OR s.isVip = :isVip) AND " +
           "(:isFree IS NULL OR s.isFree = :isFree) AND " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:chapterRange IS NULL OR " +
           "CASE :chapterRange " +
           "WHEN 'under300' THEN s.chapterCount < 300 " +
           "WHEN '300-600' THEN s.chapterCount >= 300 AND s.chapterCount < 600 " +
           "WHEN '600-1000' THEN s.chapterCount >= 600 AND s.chapterCount < 1000 " +
           "WHEN '1000-1500' THEN s.chapterCount >= 1000 AND s.chapterCount < 1500 " +
           "WHEN 'over1500' THEN s.chapterCount >= 1500 " +
           "ELSE " +
           "CASE WHEN :chapterRange LIKE '%-%' THEN " +
           "  s.chapterCount >= CAST(SUBSTRING(:chapterRange, 1, LOCATE('-', :chapterRange) - 1) AS INTEGER) AND " +
           "  s.chapterCount < CAST(SUBSTRING(:chapterRange, LOCATE('-', :chapterRange) + 1) AS INTEGER) " +
           "ELSE true END " +
           "END) AND " +
           "(:tagNames IS NULL OR LOWER(t.name) IN :tagNames) AND " +
           "(:keyword IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.author) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Story> findByFilters(@Param("genreName") String genreName, 
                             @Param("isVip") Boolean isVip, 
                             @Param("isFree") Boolean isFree, 
                             @Param("status") StoryStatus status, 
                             @Param("chapterRange") String chapterRange,
                             @Param("tagNames") List<String> tagNames,
                             @Param("keyword") String keyword,
                             Pageable pageable);
} 