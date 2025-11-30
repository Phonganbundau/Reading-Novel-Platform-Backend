package com.truyenchu.demo.service;

import com.truyenchu.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface UserService {
    User registerUser(String username, String email, String password);
    User findByUsername(String username);
    User save(User user);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Page<User> findAll(Pageable pageable);
    User findById(Long id);
    void deleteById(Long id);
    void updateCoins(Long userId, BigDecimal amount);
    void deductCoins(Long userId, BigDecimal amount);
    User getUserById(Long userId);
    void updateAvatar(String username, String avatarUrl);
    void updateBanner(String username, String bannerUrl);
    void incrementChaptersRead(String username);
} 