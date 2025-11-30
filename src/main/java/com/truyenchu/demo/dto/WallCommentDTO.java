package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.WallComment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class WallCommentDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDTO user;
    private UserDTO userPost;
    private Long parentCommentId;
    private List<WallCommentDTO> replies;
    private int replyCount;

    public static WallCommentDTO fromEntity(WallComment wallComment) {
        if (wallComment == null) {
            return null;
        }

        WallCommentDTO dto = new WallCommentDTO();
        dto.setId(wallComment.getId());
        dto.setContent(wallComment.getContent());
        dto.setCreatedAt(wallComment.getCreatedAt());
        dto.setUpdatedAt(wallComment.getUpdatedAt());
        
        if (wallComment.getUser() != null) {
            dto.setUser(UserDTO.fromEntity(wallComment.getUser()));
        }
        
        if (wallComment.getUserPost() != null) {
            dto.setUserPost(UserDTO.fromEntity(wallComment.getUserPost()));
        }
        
        if (wallComment.getParentComment() != null) {
            dto.setParentCommentId(wallComment.getParentComment().getId());
        }
        
        if (wallComment.getReplies() != null) {
            List<WallCommentDTO> replyDtos = wallComment.getReplies().stream()
                .map(WallCommentDTO::fromEntity)
                .collect(Collectors.toList());
            dto.setReplies(replyDtos);
            dto.setReplyCount(replyDtos.size());
        }
        
        return dto;
    }
} 