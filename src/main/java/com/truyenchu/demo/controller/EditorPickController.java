package com.truyenchu.demo.controller;

import com.truyenchu.demo.dto.EditorPickDTO;
import com.truyenchu.demo.service.EditorPickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/editor-picks")
public class EditorPickController {
    @Autowired
    private EditorPickService editorPickService;

    @GetMapping
    public Page<EditorPickDTO> getEditorPicks(
            @RequestParam(required = false) String genreName,
            @PageableDefault(size = 10) Pageable pageable) {
        return editorPickService.getEditorPicks(genreName, pageable);
    }
} 