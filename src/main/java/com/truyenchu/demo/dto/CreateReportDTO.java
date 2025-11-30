package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Report;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateReportDTO {
    
    @NotNull(message = "Report type is required")
    private Report.ReportType type;
    
    private Long storyId;
    private Integer chapterNumber;
    
    @NotBlank(message = "Content is required")
    private String content;
} 