package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Transaction;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTransactionStatusDTO {
    
    @NotNull(message = "Status is required")
    private Transaction.TransactionStatus status;
    
    private String adminNote; // Ghi chú của admin khi cập nhật status
} 