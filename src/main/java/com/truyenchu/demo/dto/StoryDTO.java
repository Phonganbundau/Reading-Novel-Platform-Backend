package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Genre;
import com.truyenchu.demo.entity.Story;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StoryDTO {
    private String title;
    private String description;
    private String coverUrl;
    private Genre genre;
    private Story.StoryStatus status;
    private boolean isVip;
    private boolean isFree;
} 