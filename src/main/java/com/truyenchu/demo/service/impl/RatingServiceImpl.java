package com.truyenchu.demo.service.impl;

import com.truyenchu.demo.entity.Rating;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.repository.RatingRepository;
import com.truyenchu.demo.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;

    @Override
    public Rating rateStory(Story story, User user, Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        
        Rating existingRating = ratingRepository.findByStoryAndUser(story, user)
                .orElse(new Rating());
        
        existingRating.setStory(story);
        existingRating.setUser(user);
        existingRating.setRating(rating);
        
        return ratingRepository.save(existingRating);
    }

    @Override
    public Rating updateRating(Long id, Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        
        Rating existingRating = findById(id);
        existingRating.setRating(rating);
        return ratingRepository.save(existingRating);
    }

    @Override
    public void deleteRating(Long id) {
        ratingRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Rating findById(Long id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rating not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Rating findByStoryAndUser(Story story, User user) {
        return ratingRepository.findByStoryAndUser(story, user)
                .orElseThrow(() -> new RuntimeException("Rating not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRatingByStory(Story story) {
        return ratingRepository.getAverageRatingByStory(story);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStory(Story story) {
        return ratingRepository.countByStory(story);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rating> findTopRatingsByStory(Story story) {
        return ratingRepository.findTopRatingsByStory(story);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserRated(Story story, User user) {
        return ratingRepository.findByStoryAndUser(story, user).isPresent();
    }
} 