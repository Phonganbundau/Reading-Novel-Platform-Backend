package com.truyenchu.demo.repository;

import com.truyenchu.demo.entity.Follow;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByUserAndStory(User user, Story story);
    
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.story = :story")
    long countByStory(@Param("story") Story story);
    
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.user = :user")
    long countByUser(@Param("user") User user);
    
    void deleteByUserAndStory(User user, Story story);

    List<Follow> findByUser(User user);
    
    List<Follow> findByStory(Story story);
    
    // Thống kê theo thời gian
    long countByStoryAndCreatedAtAfter(Story story, LocalDateTime dateTime);
} 