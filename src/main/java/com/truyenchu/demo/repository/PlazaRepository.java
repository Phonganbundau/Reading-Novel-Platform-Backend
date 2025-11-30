package com.truyenchu.demo.repository;

import com.truyenchu.demo.entity.Plaza;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlazaRepository extends JpaRepository<Plaza, Long> {
    
    // Tìm tất cả plaza gốc (không có parent)
    List<Plaza> findByParentPlazaIsNullOrderByCreatedAtDesc();
    
    // Tìm tất cả plaza theo root plaza
    List<Plaza> findByRootPlazaOrderByCreatedAtAsc(Plaza rootPlaza);
    
    // Tìm tất cả plaza theo user
    List<Plaza> findByUserOrderByCreatedAtDesc(com.truyenchu.demo.entity.User user);
    
    // Tìm tất cả plaza theo story
    List<Plaza> findByStoryOrderByCreatedAtDesc(com.truyenchu.demo.entity.Story story);
    
    // Tìm tất cả plaza theo story với phân trang
    Page<Plaza> findByStoryOrderByCreatedAtDesc(com.truyenchu.demo.entity.Story story, Pageable pageable);
    
    // Đếm số plaza theo user
    long countByUser(com.truyenchu.demo.entity.User user);
    
    // Tìm plaza gốc theo story
    List<Plaza> findByStoryAndParentPlazaIsNullOrderByCreatedAtDesc(com.truyenchu.demo.entity.Story story);
    
    // Tìm plaza gốc theo story với phân trang
    Page<Plaza> findByStoryAndParentPlazaIsNullOrderByCreatedAtDesc(com.truyenchu.demo.entity.Story story, Pageable pageable);
    
    // Tìm plaza theo user với phân trang
    Page<Plaza> findByUserOrderByCreatedAtDesc(com.truyenchu.demo.entity.User user, Pageable pageable);
    
    // Đếm số plaza theo story
    long countByStory(com.truyenchu.demo.entity.Story story);

    Page<Plaza> findByParentPlazaIsNullOrderByCreatedAtDesc(org.springframework.data.domain.Pageable pageable);
} 