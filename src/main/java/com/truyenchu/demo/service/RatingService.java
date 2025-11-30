package com.truyenchu.demo.service;

import com.truyenchu.demo.entity.Rating;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;


import java.util.List;

public interface RatingService {
    Rating rateStory(Story story, User user, Integer rating);
    Rating updateRating(Long id, Integer rating);
    void deleteRating(Long id);
    Rating findById(Long id);
    Rating findByStoryAndUser(Story story, User user);
    Double getAverageRatingByStory(Story story);
    long countByStory(Story story);
    List<Rating> findTopRatingsByStory(Story story);
    boolean hasUserRated(Story story, User user);
} 