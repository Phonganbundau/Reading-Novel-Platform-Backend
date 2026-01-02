package com.truyenchu.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewChapterNotificationMessage implements Serializable {
    private Long storyId;
    private String storyTitle;
    private Integer chapterNumber;
    private String chapterTitle;
    private Long translatorId;
    private Long followerId;
}

