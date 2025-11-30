package com.truyenchu.demo.service;

import com.truyenchu.demo.entity.Genre;
import java.util.List;

public interface GenreService {
    List<Genre> findAll();
    Genre findById(Long id);
    Genre createGenre(Genre genre);
    Genre updateGenre(Long id, Genre genre);
    void deleteGenre(Long id);
} 