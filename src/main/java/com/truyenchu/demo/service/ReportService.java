package com.truyenchu.demo.service;

import com.truyenchu.demo.dto.CreateReportDTO;
import com.truyenchu.demo.dto.ReportDTO;
import com.truyenchu.demo.dto.UpdateReportStatusDTO;
import com.truyenchu.demo.entity.Report;
import com.truyenchu.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ReportService {
    
    // Tạo report mới
    ReportDTO createReport(CreateReportDTO createReportDTO, String username);
    
    // Lấy report theo ID
    ReportDTO getReportById(Long id);
    
    // Cập nhật status của report (chỉ admin)
    ReportDTO updateReportStatus(Long id, UpdateReportStatusDTO updateDTO);
    
    // Xóa report (chỉ admin)
    void deleteReport(Long id);
    
    // Lấy tất cả reports với phân trang
    Page<ReportDTO> getAllReports(Pageable pageable);
    
    // Lấy reports theo status
    Page<ReportDTO> getReportsByStatus(Report.ReportStatus status, Pageable pageable);
    
    // Lấy reports theo type
    Page<ReportDTO> getReportsByType(Report.ReportType type, Pageable pageable);
    
    // Lấy reports theo user
    Page<ReportDTO> getReportsByUser(String username, Pageable pageable);
    
    // Lấy reports theo story
    Page<ReportDTO> getReportsByStory(Long storyId, Pageable pageable);
    
    // Lấy reports theo chapter
    Page<ReportDTO> getReportsByChapter(Long storyId, Integer chapterNumber, Pageable pageable);
    
    // Lấy reports gần đây nhất
    Page<ReportDTO> getRecentReports(Pageable pageable);
    
    // Lấy reports theo khoảng thời gian
    Page<ReportDTO> getReportsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    // Thống kê reports theo status
    Map<String, Long> getReportStatsByStatus();
    
    // Thống kê reports theo type
    Map<String, Long> getReportStatsByType();
    
    // Kiểm tra user đã report story chưa
    boolean hasUserReportedStory(String username, Long storyId);
    
    // Kiểm tra user đã report chapter chưa
    boolean hasUserReportedChapter(String username, Long storyId, Integer chapterNumber);
    
    // Lấy số lượng reports theo story
    long getReportCountByStory(Long storyId);
    
    // Lấy số lượng reports theo chapter
    long getReportCountByChapter(Long storyId, Integer chapterNumber);
} 