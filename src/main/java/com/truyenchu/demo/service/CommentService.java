package com.truyenchu.demo.service;

import com.truyenchu.demo.entity.Comment;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    Comment createComment(String content, Story story, User user);
    Comment updateComment(Long id, String content);
    void deleteComment(Long id);
    Comment findById(Long id);
    Page<Comment> findByStory(Story story, Pageable pageable);
    Page<Comment> findByUser(User user, Pageable pageable);
    Page<Comment> findLatestCommentsByStory(Story story, Pageable pageable);
    long countByStory(Story story);
} 