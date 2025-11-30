package com.truyenchu.demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTransactionDTO {
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1000.0", message = "Minimum withdrawal amount is 1000")
    private BigDecimal amount;
    
    private String note; // Ghi chú của translator
} 