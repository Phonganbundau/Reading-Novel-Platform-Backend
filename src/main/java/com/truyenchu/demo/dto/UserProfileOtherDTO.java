package com.truyenchu.demo.dto;
import lombok.Data;


@Data
public class UserProfileOtherDTO {
    private String username;
    private String avatar;
    private String banner;
    private String title;
    private UserStatsDTO stats;


} 


