package com.truyenchu.demo.controller;

import com.truyenchu.demo.dto.RatingDTO;
import com.truyenchu.demo.dto.RatingListDTO;
import com.truyenchu.demo.dto.RatingRequest;
import com.truyenchu.demo.entity.Rating;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.repository.RatingRepository;
import com.truyenchu.demo.repository.StoryRepository;
import com.truyenchu.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;

    @GetMapping
    public ResponseEntity<Page<RatingListDTO>> getAllRatings(Pageable pageable) {
        Page<Rating> ratings = ratingRepository.findAllByOrderByCreatedAtDesc(pageable);
        Page<RatingListDTO> ratingDtos = ratings.map(RatingListDTO::fromEntity);
        return ResponseEntity.ok(ratingDtos);
    }

    @GetMapping("/story/{storyId}")
    public ResponseEntity<Page<RatingDTO>> getRatingsByStory(@PathVariable Long storyId, Pageable pageable) {
        Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new RuntimeException("Truyện không tồn tại"));
        
        Page<Rating> ratingsPage = ratingRepository.findByStoryOrderByCreatedAtDesc(story, pageable);
        Page<RatingDTO> ratingDtos = ratingsPage.map(this::convertToDTO);
        
        return ResponseEntity.ok(ratingDtos);
    }

    @PostMapping("/story/{storyId}")
    public ResponseEntity<RatingDTO> addRating(
            @PathVariable Long storyId,
            @RequestBody RatingRequest request) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        
        Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new RuntimeException("Truyện không tồn tại"));

        // Check if user already rated this story
        ratingRepository.findByUserAndStory(user, story).ifPresent(existingRating -> {
            throw new RuntimeException("Bạn đã đánh giá truyện này rồi");
        });

        Rating newRating = new Rating();
        newRating.setUser(user);
        newRating.setStory(story);
        newRating.setRating(request.getRating());
        newRating.setComment(request.getComment());
        newRating.setCreatedAt(LocalDateTime.now());
        newRating.setUpdatedAt(LocalDateTime.now());

        Rating savedRating = ratingRepository.save(newRating);
        return ResponseEntity.ok(convertToDTO(savedRating));
    }

    @PutMapping("/story/{storyId}")
    public ResponseEntity<RatingDTO> updateRating(
            @PathVariable Long storyId,
            @RequestBody RatingRequest request) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new RuntimeException("Story not found"));
        
        Rating existingRating = ratingRepository.findByUserAndStory(user, story)
            .orElseThrow(() -> new RuntimeException("Rating not found"));
        
        existingRating.setRating(request.getRating());
        existingRating.setComment(request.getComment());
        existingRating.setUpdatedAt(LocalDateTime.now());
        
        Rating updatedRating = ratingRepository.save(existingRating);
        return ResponseEntity.ok(convertToDTO(updatedRating));
    }

    @DeleteMapping("/story/{storyId}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long storyId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new RuntimeException("Story not found"));
        
        Rating rating = ratingRepository.findByUserAndStory(user, story)
            .orElseThrow(() -> new RuntimeException("Rating not found"));
        
        ratingRepository.delete(rating);
        return ResponseEntity.ok().build();
    }

    private RatingDTO convertToDTO(Rating rating) {
        RatingDTO dto = new RatingDTO();
        dto.setId(rating.getId());
        dto.setRating(rating.getRating());
        dto.setCreatedAt(rating.getCreatedAt());
        dto.setUpdatedAt(rating.getUpdatedAt());
        dto.setUserId(rating.getUser().getId());
        dto.setUsername(rating.getUser().getUsername());
        dto.setUserAvatar(rating.getUser().getAvatar());
        dto.setStoryId(rating.getStory().getId());
        dto.setComment(rating.getComment());
        return dto;
    }
} 