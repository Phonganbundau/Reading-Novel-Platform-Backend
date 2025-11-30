package com.truyenchu.demo.service.impl;

import com.truyenchu.demo.dto.PlazaLikeResponse;
import com.truyenchu.demo.entity.Plaza;
import com.truyenchu.demo.entity.PlazaLike;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.repository.PlazaLikeRepository;
import com.truyenchu.demo.repository.PlazaRepository;
import com.truyenchu.demo.service.PlazaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlazaServiceImpl implements PlazaService {
    private final PlazaRepository plazaRepository;
    private final PlazaLikeRepository plazaLikeRepository;

    @Override
    public Plaza createPlaza(String content, Story story, User user, Plaza parentPlaza) {
        Plaza plaza = new Plaza();
        plaza.setContent(content);
        plaza.setStory(story);
        plaza.setUser(user);
        plaza.setParentPlaza(parentPlaza);
        
        // Nếu có parent plaza, cập nhật replies count
        if (parentPlaza != null) {
            incrementRepliesCount(parentPlaza.getId());
        }
        
        return plazaRepository.save(plaza);
    }

    @Override
    public Plaza updatePlaza(Long id, String content) {
        Plaza plaza = findById(id);
        plaza.setContent(content);
        return plazaRepository.save(plaza);
    }

    @Override
    public void deletePlaza(Long id) {
        Plaza plaza = findById(id);
        
        // Nếu có parent plaza, giảm replies count
        if (plaza.getParentPlaza() != null) {
            decrementRepliesCount(plaza.getParentPlaza().getId());
        }
        
        plazaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Plaza findById(Long id) {
        return plazaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plaza not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Plaza> findRootPlazasByStory(Story story, Pageable pageable) {
        return plazaRepository.findByStoryAndParentPlazaIsNullOrderByCreatedAtDesc(story, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Plaza> findByUser(User user, Pageable pageable) {
        return plazaRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Plaza> findByRootPlaza(Plaza rootPlaza) {
        return plazaRepository.findByRootPlazaOrderByCreatedAtAsc(rootPlaza);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStory(Story story) {
        return plazaRepository.countByStory(story);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByUser(User user) {
        return plazaRepository.countByUser(user);
    }

    @Override
    public void incrementLikesCount(Long plazaId) {
        Plaza plaza = findById(plazaId);
        plaza.setLikesCount(plaza.getLikesCount() + 1);
        plazaRepository.save(plaza);
    }

    @Override
    public void decrementLikesCount(Long plazaId) {
        Plaza plaza = findById(plazaId);
        if (plaza.getLikesCount() > 0) {
            plaza.setLikesCount(plaza.getLikesCount() - 1);
            plazaRepository.save(plaza);
        }
    }

    @Override
    public void incrementRepliesCount(Long plazaId) {
        Plaza plaza = findById(plazaId);
        plaza.setRepliesCount(plaza.getRepliesCount() + 1);
        plazaRepository.save(plaza);
    }

    @Override
    public void decrementRepliesCount(Long plazaId) {
        Plaza plaza = findById(plazaId);
        if (plaza.getRepliesCount() > 0) {
            plaza.setRepliesCount(plaza.getRepliesCount() - 1);
            plazaRepository.save(plaza);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Plaza> findAllPlazas(Pageable pageable) {
        return plazaRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Plaza> findRootPlazas(Pageable pageable) {
        return plazaRepository.findByParentPlazaIsNullOrderByCreatedAtDesc(pageable);
    }

    @Override
    public PlazaLikeResponse toggleLike(Long plazaId, Long userId) {
        boolean isLiked = plazaLikeRepository.existsByPlazaIdAndUserId(plazaId, userId);
        
        if (isLiked) {
            // Unlike
            plazaLikeRepository.deleteByPlazaIdAndUserId(plazaId, userId);
            decrementLikesCount(plazaId);
            
            Plaza plaza = findById(plazaId);
            return new PlazaLikeResponse(false, plaza.getLikesCount());
        } else {
            // Like
            Plaza plaza = findById(plazaId);
            User user = new User();
            user.setId(userId);
            
            PlazaLike plazaLike = new PlazaLike();
            plazaLike.setPlaza(plaza);
            plazaLike.setUser(user);
            plazaLikeRepository.save(plazaLike);
            
            incrementLikesCount(plazaId);
            
            // Get updated plaza to get the new likes count
            plaza = findById(plazaId);
            return new PlazaLikeResponse(true, plaza.getLikesCount());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLikedByUser(Long plazaId, Long userId) {
        return plazaLikeRepository.existsByPlazaIdAndUserId(plazaId, userId);
    }
} 