package com.truyenchu.demo.dto;

import lombok.Data;

@Data
public class PlazaRequest {
    private String content;
    private Long storyId;
    private Long parentPlazaId; 
} 