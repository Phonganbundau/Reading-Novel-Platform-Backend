package com.truyenchu.demo.repository;

import com.truyenchu.demo.entity.PlazaLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlazaLikeRepository extends JpaRepository<PlazaLike, Long> {
    
    // Kiểm tra xem user đã like plaza chưa
    Optional<PlazaLike> findByPlazaIdAndUserId(Long plazaId, Long userId);
    
    // Đếm số like của một plaza
    long countByPlazaId(Long plazaId);
    
    // Xóa like của user cho plaza
    void deleteByPlazaIdAndUserId(Long plazaId, Long userId);
    
    // Kiểm tra xem user đã like plaza chưa (boolean)
    boolean existsByPlazaIdAndUserId(Long plazaId, Long userId);
} 