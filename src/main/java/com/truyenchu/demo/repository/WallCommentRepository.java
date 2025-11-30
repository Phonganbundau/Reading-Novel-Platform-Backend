package com.truyenchu.demo.repository;

import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.entity.WallComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WallCommentRepository extends JpaRepository<WallComment, Long> {
    
    // Lấy tất cả comment gốc (không có parent) trên wall của một user
    @Query("SELECT wc FROM WallComment wc WHERE wc.userPost = :userPost AND wc.parentComment IS NULL ORDER BY wc.createdAt DESC")
    List<WallComment> findByUserPostAndParentCommentIsNullOrderByCreatedAtDesc(@Param("userPost") User userPost);
    
    // Lấy tất cả reply của một comment
    @Query("SELECT wc FROM WallComment wc WHERE wc.parentComment = :parentComment ORDER BY wc.createdAt ASC")
    List<WallComment> findByParentCommentOrderByCreatedAtAsc(@Param("parentComment") WallComment parentComment);
    
    // Đếm số comment trên wall của một user
    long countByUserPost(User userPost);
} 