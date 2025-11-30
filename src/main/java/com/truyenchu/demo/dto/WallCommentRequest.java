package com.truyenchu.demo.dto;

import lombok.Data;

@Data
public class WallCommentRequest {
    private String content;
    private Long parentCommentId; // null nếu là comment gốc, có giá trị nếu là reply
} 