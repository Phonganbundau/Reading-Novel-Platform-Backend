package com.truyenchu.demo.dto;

import lombok.Data;

@Data
public class UserBookDTO {
    private Long id;
    private String title;
    private String cover;
    private String author;
    private String lastChapter;
    private String lastChapterNumber;
    private String lastRead;
} 