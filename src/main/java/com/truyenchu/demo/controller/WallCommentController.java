package com.truyenchu.demo.controller;

import com.truyenchu.demo.dto.WallCommentDTO;
import com.truyenchu.demo.dto.WallCommentRequest;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.service.UserService;
import com.truyenchu.demo.service.WallCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wall-comments")
@RequiredArgsConstructor
public class WallCommentController {
    private final WallCommentService wallCommentService;
    private final UserService userService;

    
    // Lấy danh sách comment trên wall của một user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WallCommentDTO>> getWallCommentsByUser(@PathVariable Long userId) {
        User userPost = userService.findById(userId);
        List<WallCommentDTO> comments = wallCommentService.getWallCommentsByUser(userPost);
        return ResponseEntity.ok(comments);
    }

    // Tạo comment mới trên wall của một user
    @PostMapping("/user/{userId}")
    public ResponseEntity<WallCommentDTO> createWallComment(
            @PathVariable Long userId,
            @RequestBody WallCommentRequest request,
            Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        User userPost = userService.findById(userId);
        
        WallCommentDTO comment = wallCommentService.createWallComment(currentUser, userPost, request.getContent(), request.getParentCommentId());
        return ResponseEntity.ok(comment);
    }

    // Reply một comment
    @PostMapping("/{commentId}/reply")
    public ResponseEntity<WallCommentDTO> replyToComment(
            @PathVariable Long commentId,
            @RequestBody WallCommentRequest request,
            Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        
        // Lấy thông tin userPost từ comment gốc
        WallCommentDTO originalComment = wallCommentService.getWallCommentsByUser(currentUser).stream()
            .filter(comment -> comment.getId().equals(commentId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        // Lấy User entity từ userPost ID
        User userPost = userService.findById(originalComment.getUserPost().getId());
        
        WallCommentDTO reply = wallCommentService.createWallComment(
            currentUser,
            userPost, 
            request.getContent(), 
            commentId
        );
        return ResponseEntity.ok(reply);
    }

    // Xóa comment
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteWallComment(
            @PathVariable Long commentId,
            Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        wallCommentService.deleteWallComment(commentId, currentUser);
        return ResponseEntity.ok().build();
    }
} 