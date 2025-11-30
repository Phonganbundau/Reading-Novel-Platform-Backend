package com.truyenchu.demo.service.impl;

import com.truyenchu.demo.dto.EditorPickDTO;
import com.truyenchu.demo.entity.EditorPick;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.repository.EditorPickRepository;
import com.truyenchu.demo.service.EditorPickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EditorPickServiceImpl implements EditorPickService {
    @Autowired
    private EditorPickRepository editorPickRepository;

    @Override
    public List<EditorPickDTO> getAllEditorPicks() {
        return editorPickRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Page<EditorPickDTO> getEditorPicks(String genre, Pageable pageable) {
        Page<EditorPick> page = editorPickRepository.findByGenreName(
            (genre == null || genre.isEmpty()) ? null : genre, pageable);
        return page.map(this::toDTO);
    }

    private EditorPickDTO toDTO(EditorPick pick) {
        Story story = pick.getStory();
        User editor = pick.getEditor();
        String authorName = story.getAuthor();
        String urlCover = story.getCoverUrl();
        Integer chapterCount = story.getChapterCount();
        String title = story.getTitle();
        Long storyId = story.getId();
        String editorName = editor.getUsername();
        String genre = story.getGenre() != null ? story.getGenre().getName() : null;
        String status = story.getStatus() != null ? story.getStatus().name() : null;
        String content = pick.getContent();
        String description = story.getDescription();
        return new EditorPickDTO(
                editorName,
                storyId,
                title,
                urlCover,
                description,
                chapterCount,
                authorName,
                genre,
                status,
                content
        );
    }
} 