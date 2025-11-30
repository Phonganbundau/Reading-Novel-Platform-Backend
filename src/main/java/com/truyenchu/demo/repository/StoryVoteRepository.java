package com.truyenchu.demo.repository;

import com.truyenchu.demo.entity.StoryVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryVoteRepository extends JpaRepository<StoryVote, Long> {
} 