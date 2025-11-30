package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Report;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateReportStatusDTO {
    
    @NotNull(message = "Status is required")
    private Report.ReportStatus status;
    
    private String adminNote; // Ghi chú của admin khi cập nhật status
} 