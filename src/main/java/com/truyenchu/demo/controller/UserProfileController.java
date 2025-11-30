package com.truyenchu.demo.controller;

import com.truyenchu.demo.dto.UserProfileDTO;
import com.truyenchu.demo.dto.UserProfileOtherDTO;
import com.truyenchu.demo.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDTO> getUserProfileById(@PathVariable Long userId) {
        return ResponseEntity.ok(userProfileService.getUserProfile(userId));
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<UserProfileDTO> getUserProfileByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userProfileService.getUserProfileByUsername(username));
    }

    @GetMapping("{userId}")
    public ResponseEntity<UserProfileOtherDTO> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userProfileService.getUserProfileOther(userId));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserProfileOtherDTO> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userProfileService.getUserProfileOtherByUsername(username));
    }
} 