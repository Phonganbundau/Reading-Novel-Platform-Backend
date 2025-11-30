package com.truyenchu.demo.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class UserProfileDTO {
    private Long id;
    private String username;
    private String avatar;
    private String banner;
    private String title;
    private String role;
    private int coinBalance;
    private int voteTicket;
    private Map<Long, List<Long>> unlockedChapters;
    private UserStatsDTO stats;
    private List<UserBookDTO> savedBooks;
    private List<UserBookDTO> readingBooks;
} 