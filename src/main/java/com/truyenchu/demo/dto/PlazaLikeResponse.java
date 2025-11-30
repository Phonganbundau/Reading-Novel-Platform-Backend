package com.truyenchu.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlazaLikeResponse {
    private boolean isLiked;
    private Long likesCount;
} 