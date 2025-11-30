package com.truyenchu.demo.service;

import com.truyenchu.demo.dto.PlazaLikeResponse;
import com.truyenchu.demo.entity.Plaza;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlazaService {
    Plaza createPlaza(String content, Story story, User user, Plaza parentPlaza);
    Plaza updatePlaza(Long id, String content);
    void deletePlaza(Long id);
    Plaza findById(Long id);
    Page<Plaza> findRootPlazasByStory(Story story, Pageable pageable);
    Page<Plaza> findByUser(User user, Pageable pageable);
    List<Plaza> findByRootPlaza(Plaza rootPlaza);
    long countByStory(Story story);
    long countByUser(User user);
    void incrementLikesCount(Long plazaId);
    void decrementLikesCount(Long plazaId);
    void incrementRepliesCount(Long plazaId);
    void decrementRepliesCount(Long plazaId);
    Page<Plaza> findAllPlazas(Pageable pageable);
    PlazaLikeResponse toggleLike(Long plazaId, Long userId);
    boolean isLikedByUser(Long plazaId, Long userId);
    Page<Plaza> findRootPlazas(Pageable pageable);
} 