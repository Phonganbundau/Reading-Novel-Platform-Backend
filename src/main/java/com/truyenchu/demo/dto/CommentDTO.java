package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Comment;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CommentDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String username;
    private String userAvatar;
    private Long storyId;
    private Long parentId;
    private Long rootCommentId;
    private List<CommentDTO> replies;
    private int replyCount;
    private int level; // Cấp độ của comment (1: gốc, 2: reply, 3: reply của reply...)

    public static CommentDTO fromEntity(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        dto.setUserAvatar(comment.getUser().getAvatar());
        dto.setStoryId(comment.getStory().getId());
        
        if (comment.getParentComment() != null) {
            dto.setParentId(comment.getParentComment().getId());
        }
        
        if (comment.getRootComment() != null) {
            dto.setRootCommentId(comment.getRootComment().getId());
        }
        
        // Tính cấp độ của comment
        dto.setLevel(calculateLevel(comment));
        
        // Chỉ hiển thị replies trực tiếp (không phải tất cả replies của replies)
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            dto.setReplies(comment.getReplies().stream()
                .map(CommentDTO::fromEntity)
                .collect(Collectors.toList()));
            dto.setReplyCount(comment.getReplies().size());
        }
        
        return dto;
    }
    
    private static int calculateLevel(Comment comment) {
        int level = 1;
        Comment current = comment;
        while (current.getParentComment() != null) {
            level++;
            current = current.getParentComment();
        }
        return level;
    }
} 