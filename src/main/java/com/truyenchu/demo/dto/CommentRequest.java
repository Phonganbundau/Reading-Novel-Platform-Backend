package com.truyenchu.demo.dto;

import lombok.Data;

@Data
public class CommentRequest {
    private String content;
    private Long parentId; // ID của comment cha (nếu là reply)
} 