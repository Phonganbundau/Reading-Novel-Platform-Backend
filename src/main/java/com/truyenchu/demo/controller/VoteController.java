package com.truyenchu.demo.controller;

import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.StoryVote;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.repository.StoryRepository;
import com.truyenchu.demo.repository.StoryVoteRepository;
import com.truyenchu.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final StoryVoteRepository storyVoteRepository;

    @PostMapping("/vote")
    public ResponseEntity<?> voteStory(@RequestBody Map<String, Object> request, Authentication authentication) {
        Long storyId = Long.valueOf(request.get("storyId").toString());
        int quantity = Integer.parseInt(request.getOrDefault("quantity", 1).toString());

        User user = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new RuntimeException("Story not found"));

        if (user.getVoteTicket() < quantity) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Bạn không đủ vé đề cử");
            response.put("voteTicket", user.getVoteTicket());
            return ResponseEntity.badRequest().body(response);
        }

        // Trừ vé đề cử
        user.setVoteTicket(user.getVoteTicket() - quantity);
        userRepository.save(user);

        // Ghi nhận vote
        StoryVote vote = new StoryVote();
        vote.setUser(user);
        vote.setStory(story);
        vote.setQuantity(quantity);
        vote.setVotedAt(LocalDateTime.now());
        storyVoteRepository.save(vote);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Đề cử thành công");
        response.put("quantity", quantity);
        return ResponseEntity.ok(response);
    }
} 