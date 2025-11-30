package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Report;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportDTO {
    private Long id;
    
    @NotNull(message = "Report type is required")
    private Report.ReportType type;
    
    private Long storyId;
    private Integer chapterNumber;
    
    @NotNull(message = "Content is required")
    private String content;
    
    private Report.ReportStatus status;
    private String userReportUsername;
    private String createdAt;
    
    // Constructor cho việc tạo report mới
    public ReportDTO(Report.ReportType type, Long storyId, Integer chapterNumber, String content) {
        this.type = type;
        this.storyId = storyId;
        this.chapterNumber = chapterNumber;
        this.content = content;
    }
    
    // Constructor mặc định
    public ReportDTO() {}
    
    // Chuyển đổi từ Entity sang DTO
    public static ReportDTO fromEntity(Report report) {
        ReportDTO dto = new ReportDTO();
        dto.setId(report.getId());
        dto.setType(report.getType());
        dto.setStoryId(report.getStoryId());
        dto.setChapterNumber(report.getChapter() != null ? report.getChapter().getChapterNumber() : null);
        dto.setContent(report.getContent());
        dto.setStatus(report.getStatus());
        dto.setUserReportUsername(report.getUserReport().getUsername());
        dto.setCreatedAt(report.getCreatedAt().toString());
        return dto;
    }
} 