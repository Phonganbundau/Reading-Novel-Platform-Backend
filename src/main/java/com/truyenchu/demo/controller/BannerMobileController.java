package com.truyenchu.demo.controller;

import com.truyenchu.demo.entity.BannerMobile;
import com.truyenchu.demo.repository.BannerMobileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banners-mobile")
@RequiredArgsConstructor
public class BannerMobileController {
    private final BannerMobileRepository bannerMobileRepository;

    @GetMapping
    public ResponseEntity<List<BannerMobile>> getAllBannersMobile() {
        List<BannerMobile> banners = bannerMobileRepository.findTop3ByOrderByCreatedAtDesc();
        return ResponseEntity.ok(banners);
    }

    @PostMapping
    public ResponseEntity<BannerMobile> createBannerMobile(@RequestBody BannerMobile bannerMobile) {
        BannerMobile saved = bannerMobileRepository.save(bannerMobile);
        return ResponseEntity.ok(saved);
    }
} 