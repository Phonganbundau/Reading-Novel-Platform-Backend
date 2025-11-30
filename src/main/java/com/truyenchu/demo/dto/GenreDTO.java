package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Genre;
import lombok.Data;

@Data
public class GenreDTO {
    private Long id;
    private String name;
    private String description;

    public static GenreDTO fromEntity(Genre genre) {
        GenreDTO dto = new GenreDTO();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }
} 