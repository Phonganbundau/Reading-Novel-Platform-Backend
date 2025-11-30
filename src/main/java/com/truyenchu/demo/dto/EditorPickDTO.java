package com.truyenchu.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditorPickDTO {
    private String editorName;
    private Long storyId;
    private String storyTitle;
    private String coverUrl;
    private String description;
    private Integer chapterCount;
    private String authorName;
    private String genre;
    private String status;
    private String content;
} 