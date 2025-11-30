package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Tag;
import lombok.Data;

@Data
public class TagDTO {
    private Long id;
    private String name;

    public static TagDTO fromEntity(Tag tag) {
        TagDTO dto = new TagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        return dto;
    }
} 

