package com.truyenchu.demo.repository;

import com.truyenchu.demo.entity.BannerMobile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerMobileRepository extends JpaRepository<BannerMobile, Long> {
    List<BannerMobile> findTop3ByOrderByCreatedAtDesc();
} 