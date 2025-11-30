package com.truyenchu.demo.controller;

import com.truyenchu.demo.entity.Banner;
import com.truyenchu.demo.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
public class BannerController {
    private final BannerRepository bannerRepository;

    @GetMapping
    public ResponseEntity<List<Banner>> getAllBanners() {
        List<Banner> banners = bannerRepository.findTop3ByOrderByCreatedAtDesc();
        return ResponseEntity.ok(banners);
    }

    @PostMapping
    public ResponseEntity<Banner> createBanner(@RequestBody Banner banner) {
        Banner saved = bannerRepository.save(banner);
        return ResponseEntity.ok(saved);
    }
} 