package com.truyenchu.demo.controller;

import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.service.StoryService;
import com.truyenchu.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {
    private final StoryService storyService;
    private final UserService userService;
  
    @PostMapping("/story/{storyId}")
    public ResponseEntity<?> followStory(@PathVariable Long storyId, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        storyService.followStory(storyId, user);
        

        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully followed the story");
        response.put("followerCount", storyService.getFollowerCount(storyId));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/story/{storyId}")
    public ResponseEntity<?> unfollowStory(@PathVariable Long storyId, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        storyService.unfollowStory(storyId, user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully unfollowed the story");
        response.put("followerCount", storyService.getFollowerCount(storyId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stories/{storyId}/status")
    public ResponseEntity<?> getFollowStatus(@PathVariable Long storyId, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        boolean isFollowing = storyService.isFollowing(storyId, user);
        long followerCount = storyService.getFollowerCount(storyId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("isFollowing", isFollowing);
        response.put("followerCount", followerCount);
        return ResponseEntity.ok(response);
    }
} 