package com.truyenchu.demo.service.impl;

import com.truyenchu.demo.entity.Comment;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.repository.CommentRepository;
import com.truyenchu.demo.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Override
    public Comment createComment(String content, Story story, User user) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setStory(story);
        comment.setUser(user);
        return commentRepository.save(comment);
    }

    @Override
    public Comment updateComment(Long id, String content) {
        Comment comment = findById(id);
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Comment> findByStory(Story story, Pageable pageable) {
        return commentRepository.findByStory(story, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Comment> findByUser(User user, Pageable pageable) {
        return commentRepository.findByUser(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Comment> findLatestCommentsByStory(Story story, Pageable pageable) {
        return commentRepository.findLatestCommentsByStory(story, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStory(Story story) {
        return commentRepository.countByStory(story);
    }
} 