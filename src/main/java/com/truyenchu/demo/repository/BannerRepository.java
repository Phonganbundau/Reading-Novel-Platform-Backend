package com.truyenchu.demo.repository;

import com.truyenchu.demo.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findTop3ByOrderByCreatedAtDesc();
} 