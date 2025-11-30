package com.truyenchu.demo.repository;

import com.truyenchu.demo.entity.Rating;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByStoryOrderByCreatedAtDesc(Story story);
    Optional<Rating> findByUserAndStory(User user, Story story);
    Optional<Rating> findByStoryAndUser(Story story, User user);
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.story = :story")
    Double getAverageRatingByStory(@Param("story") Story story);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.story = :story")
    long countByStory(@Param("story") Story story);
    
    @Query("SELECT r FROM Rating r WHERE r.story = :story ORDER BY r.rating DESC")
    java.util.List<Rating> findTopRatingsByStory(@Param("story") Story story);

    // Lấy tất cả đánh giá với phân trang
    Page<Rating> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Rating> findByStoryOrderByCreatedAtDesc(Story story, Pageable pageable);
} 