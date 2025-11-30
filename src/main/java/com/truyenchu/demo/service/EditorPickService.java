package com.truyenchu.demo.service;

import com.truyenchu.demo.dto.EditorPickDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EditorPickService {
    List<EditorPickDTO> getAllEditorPicks();
    Page<EditorPickDTO> getEditorPicks(String genre, Pageable pageable);
} 