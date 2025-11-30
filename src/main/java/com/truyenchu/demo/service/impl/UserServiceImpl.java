package com.truyenchu.demo.service.impl;

import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.exception.BusinessException;
import com.truyenchu.demo.exception.ResourceNotFoundException;
import com.truyenchu.demo.repository.UserRepository;
import com.truyenchu.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(User.UserRole.USER);
        user.setCoinBalance(BigDecimal.ZERO);
        user.setCoinEarning(BigDecimal.ZERO);
        user.setVoteTicket(0);

        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateCoins(Long userId, BigDecimal amount) {
        User user = findById(userId);
        user.setCoinBalance(user.getCoinBalance().add(amount));
        userRepository.save(user);
    }

    @Override
    public void deductCoins(Long userId, BigDecimal amount) {
        User user = findById(userId);
        if (user.getCoinBalance().compareTo(amount) < 0) {
            throw new BusinessException("Insufficient coins");
        }
        user.setCoinBalance(user.getCoinBalance().subtract(amount));
        userRepository.save(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public void updateAvatar(String username, String avatarUrl) {
        User user = findByUsername(username);
        user.setAvatar(avatarUrl);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void incrementChaptersRead(String username) {
        User user = findByUsername(username);
        user.setChaptersRead(user.getChaptersRead() + 1);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateBanner(String username, String bannerUrl) {
        User user = findByUsername(username);
        user.setBanner(bannerUrl);
        userRepository.save(user);
    }
} 