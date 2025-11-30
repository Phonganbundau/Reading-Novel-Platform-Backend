package com.truyenchu.demo.repository;

import com.truyenchu.demo.entity.Comment;
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
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByStory(Story story, Pageable pageable);
    
    Page<Comment> findByUser(User user, Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.story = :story")
    long countByStory(@Param("story") Story story);
    
    @Query("SELECT c FROM Comment c WHERE c.story = :story ORDER BY c.createdAt DESC")
    Page<Comment> findLatestCommentsByStory(@Param("story") Story story, Pageable pageable);

    List<Comment> findByStoryOrderByCreatedAtDesc(Story story);

    List<Comment> findByStoryAndParentCommentIsNullOrderByCreatedAtDesc(Story story);

    Optional<Comment> findByUserAndStory(User user, Story story);
    
    // Lấy tất cả replies của một root comment (bao gồm cả replies của replies)
    @Query("SELECT c FROM Comment c WHERE c.rootComment.id = :rootCommentId AND c.id != :rootCommentId ORDER BY c.createdAt ASC")
    List<Comment> findAllRepliesByRootComment(@Param("rootCommentId") Long rootCommentId);
    
    // Lấy replies trực tiếp của một comment
    List<Comment> findByParentCommentOrderByCreatedAtAsc(Comment parentComment);

    Page<Comment> findByStoryAndParentCommentIsNullOrderByCreatedAtDesc(Story story, Pageable pageable);
} 