package com.truyenchu.demo.controller;

import com.truyenchu.demo.dto.CommentDTO;
import com.truyenchu.demo.dto.CommentRequest;
import com.truyenchu.demo.entity.Comment;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;

import com.truyenchu.demo.repository.CommentRepository;
import com.truyenchu.demo.repository.StoryRepository;
import com.truyenchu.demo.repository.UserRepository;
import com.truyenchu.demo.service.NotificationService;
import lombok.RequiredArgsConstructor;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;
    private final NotificationService notificationService;

    @GetMapping("/story/{storyId}")
    public ResponseEntity<Page<CommentDTO>> getCommentsByStory(@PathVariable Long storyId, Pageable pageable) {
        Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new RuntimeException("Story not found"));
        
        // Lấy chỉ những comment gốc (không có parent) với phân trang
        Page<Comment> rootCommentsPage = commentRepository.findByStoryAndParentCommentIsNullOrderByCreatedAtDesc(story, pageable);
        
        Page<CommentDTO> commentsPage = rootCommentsPage.map(rootComment -> {
            CommentDTO dto = CommentDTO.fromEntity(rootComment);
            
            // Lấy tất cả replies của root comment này
            List<Comment> allReplies = commentRepository.findAllRepliesByRootComment(rootComment.getId());
            List<CommentDTO> replyDtos = allReplies.stream()
                .map(CommentDTO::fromEntity)
                .collect(Collectors.toList());
            
            dto.setReplies(replyDtos);
            dto.setReplyCount(allReplies.size());
            
            return dto;
        });
        
        return ResponseEntity.ok(commentsPage);
    }

    @PostMapping("/story/{storyId}")
    public ResponseEntity<CommentDTO> addComment(
            @PathVariable Long storyId,
            @RequestBody CommentRequest request) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new RuntimeException("Story not found"));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setStory(story);
        comment.setContent(request.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        // Nếu là reply, set parent comment và root comment
        if (request.getParentId() != null) {
            Comment parentComment = commentRepository.findById(request.getParentId())
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            comment.setParentComment(parentComment);
            
            // Tự động set root comment
            if (parentComment.getRootComment() != null) {
                comment.setRootComment(parentComment.getRootComment());
            } else {
                comment.setRootComment(parentComment);
            }

           
            
            // Tạo thông báo cho user được reply
            notificationService.createCommentReplyNotification(
                parentComment.getUser(), 
                user, 
                parentComment, 
                comment
            );
        }

        Comment savedComment = commentRepository.save(comment);
        return ResponseEntity.ok(CommentDTO.fromEntity(savedComment));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest request) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
  
        
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        // Kiểm tra xem comment có phải của user hiện tại không
        if (!comment.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You can only update your own comments");
        }
        
        comment.setContent(request.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        
        Comment updatedComment = commentRepository.save(comment);
        return ResponseEntity.ok(CommentDTO.fromEntity(updatedComment));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        // Kiểm tra xem comment có phải của user hiện tại không
        if (!comment.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You can only delete your own comments");
        }
        
        commentRepository.delete(comment);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<CommentDTO>> getCommentReplies(@PathVariable Long commentId) {
        Comment parentComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        List<CommentDTO> replies = parentComment.getReplies().stream()
            .map(CommentDTO::fromEntity)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(replies);
    }
} 