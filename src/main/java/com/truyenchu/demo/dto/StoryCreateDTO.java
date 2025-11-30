package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Story;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class StoryCreateDTO {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Author is required")
    private String author;

    @NotNull(message = "Genre is required")
    private Long genreId;

    private Set<Long> tagIds;

    @NotNull(message = "Status is required")
    private Story.StoryStatus status;

    @NotBlank(message = "Cover URL is required")
    private String coverUrl;


    private boolean isVip = false;


    private boolean isFree = true;
}
