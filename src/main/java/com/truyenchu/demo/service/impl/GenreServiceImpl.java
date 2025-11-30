package com.truyenchu.demo.service.impl;

import com.truyenchu.demo.entity.Genre;
import com.truyenchu.demo.repository.GenreRepository;
import com.truyenchu.demo.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Genre findById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found"));
    }

    @Override
    public Genre createGenre(Genre genre) {
        if (genreRepository.existsByName(genre.getName())) {
            throw new RuntimeException("Genre with this name already exists");
        }
        return genreRepository.save(genre);
    }

    @Override
    public Genre updateGenre(Long id, Genre genreDetails) {
        Genre genre = findById(id);
        if (!genre.getName().equals(genreDetails.getName()) && 
            genreRepository.existsByName(genreDetails.getName())) {
            throw new RuntimeException("Genre with this name already exists");
        }
        genre.setName(genreDetails.getName());
        return genreRepository.save(genre);
    }

    @Override
    public void deleteGenre(Long id) {
        genreRepository.deleteById(id);
    }
} 