package com.truyenchu.demo.service.impl;

import com.truyenchu.demo.dto.TagDTO;
import com.truyenchu.demo.entity.Tag;
import com.truyenchu.demo.repository.TagRepository;
import com.truyenchu.demo.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    @Override
    public List<TagDTO> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        return tags.stream().map(TagDTO::fromEntity).collect(Collectors.toList());
    }
} 