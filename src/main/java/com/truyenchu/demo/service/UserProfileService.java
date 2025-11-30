package com.truyenchu.demo.service;

import com.truyenchu.demo.dto.UserProfileDTO;
import com.truyenchu.demo.dto.UserProfileOtherDTO;

public interface UserProfileService {
    UserProfileDTO getUserProfile(Long userId);
    UserProfileDTO getUserProfileByUsername(String username);
    UserProfileOtherDTO getUserProfileOther(Long userId);
    UserProfileOtherDTO getUserProfileOtherByUsername(String username);
} 