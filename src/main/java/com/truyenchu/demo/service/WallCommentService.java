package com.truyenchu.demo.service;

import com.truyenchu.demo.dto.WallCommentDTO;
import com.truyenchu.demo.entity.User;

import java.util.List;

public interface WallCommentService {
    List<WallCommentDTO> getWallCommentsByUser(User userPost);
    WallCommentDTO createWallComment(User currentUser, User userPost, String content, Long parentCommentId);
    void deleteWallComment(Long commentId, User currentUser);
} 