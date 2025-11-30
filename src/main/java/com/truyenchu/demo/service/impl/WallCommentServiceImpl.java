package com.truyenchu.demo.service.impl;

import com.truyenchu.demo.dto.WallCommentDTO;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.entity.WallComment;
import com.truyenchu.demo.repository.WallCommentRepository;
import com.truyenchu.demo.service.NotificationService;
import com.truyenchu.demo.service.WallCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WallCommentServiceImpl implements WallCommentService {
    private final WallCommentRepository wallCommentRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public List<WallCommentDTO> getWallCommentsByUser(User userPost) {
        List<WallComment> rootComments = wallCommentRepository.findByUserPostAndParentCommentIsNullOrderByCreatedAtDesc(userPost);
        
        return rootComments.stream().map(rootComment -> {
            WallCommentDTO dto = WallCommentDTO.fromEntity(rootComment);
            
            // Lấy tất cả replies của root comment này
            List<WallComment> replies = wallCommentRepository.findByParentCommentOrderByCreatedAtAsc(rootComment);
            List<WallCommentDTO> replyDtos = replies.stream()
                .map(WallCommentDTO::fromEntity)
                .collect(Collectors.toList());
            
            dto.setReplies(replyDtos);
            dto.setReplyCount(replyDtos.size());
            
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public WallCommentDTO createWallComment(User currentUser, User userPost, String content, Long parentCommentId) {
        WallComment wallComment = new WallComment();
        wallComment.setUser(currentUser);
        wallComment.setUserPost(userPost);
        wallComment.setContent(content);
        
        // Nếu có parentCommentId, set parent comment
        if (parentCommentId != null) {
            WallComment parentComment = wallCommentRepository.findById(parentCommentId)
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            wallComment.setParentComment(parentComment);

            if (!parentComment.getUser().getId().equals(currentUser.getId())) {
                // Trả lời comment của người khác
                notificationService.createWallCommentReplyNotification(
                    parentComment.getUser(), 
                    currentUser, 
                    parentComment, 
                    wallComment
                );
            } else if (!userPost.getId().equals(currentUser.getId())) {
                // Trả lời comment của chính mình trên tường người khác
                notificationService.createWallCommentPostNotification(
                    userPost, 
                    currentUser, 
                    wallComment
                );
            }
        } else {
            // Tạo notification cho comment mới trên wall
            notificationService.createWallCommentPostNotification(
                userPost, 
                currentUser, 
                wallComment
            );
        }
        
        WallComment savedComment = wallCommentRepository.save(wallComment);
        return WallCommentDTO.fromEntity(savedComment);
    }

    @Override
    public void deleteWallComment(Long commentId, User currentUser) {
        WallComment wallComment = wallCommentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        // Kiểm tra xem user có quyền xóa comment không
        if (!wallComment.getUser().getId().equals(currentUser.getId()) && 
            !wallComment.getUserPost().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You don't have permission to delete this comment");
        }
        
        wallCommentRepository.delete(wallComment);
    }
} 