package com.truyenchu.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoryStatsDTO {
    private Long storyId;
    private String storyTitle;
    
    // Thống kê lượt đọc
    private Long totalViews;
    private Long todayViews;
    private Long thisWeekViews;
    private Long thisMonthViews;
    
    // Thống kê lượt đề cử (follow)
    private Long totalFollows;
    private Long todayFollows;
    private Long thisWeekFollows;
    private Long thisMonthFollows;
    
    // Thống kê lượt mở khóa
    private Long totalUnlocks;
    private Long todayUnlocks;
    private Long thisWeekUnlocks;
    private Long thisMonthUnlocks;
    
    // Thống kê tiền mở khóa
    private BigDecimal totalUnlockRevenue;
    private BigDecimal todayUnlockRevenue;
    private BigDecimal thisWeekUnlockRevenue;
    private BigDecimal thisMonthUnlockRevenue;
    
    // Thống kê tiền quà tặng
    private BigDecimal totalGiftRevenue;
    private BigDecimal todayGiftRevenue;
    private BigDecimal thisWeekGiftRevenue;
    private BigDecimal thisMonthGiftRevenue;
    
    // Tổng doanh thu
    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;
    private BigDecimal thisWeekRevenue;
    private BigDecimal thisMonthRevenue;
} 