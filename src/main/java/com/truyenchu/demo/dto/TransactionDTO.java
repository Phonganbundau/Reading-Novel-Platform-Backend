package com.truyenchu.demo.dto;

import com.truyenchu.demo.entity.Transaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private Long id;
    private Long userId;
    private String username;
    private String userAvatar;
    private String userTitle;
    private BigDecimal amount;
    private Transaction.TransactionStatus status;
    private LocalDateTime createdAt;
    private String adminNote;
    private String note; // Ghi chú của translator

    public static TransactionDTO fromEntity(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setUserId(transaction.getUser().getId());
        dto.setUsername(transaction.getUser().getUsername());
        dto.setUserAvatar(transaction.getUser().getAvatar());
        dto.setUserTitle(transaction.getUser().getTitle() != null ? transaction.getUser().getTitle().getName() : null);
        dto.setAmount(transaction.getAmount());
        dto.setStatus(transaction.getStatus());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setAdminNote(transaction.getAdminNote());
        dto.setNote(transaction.getNote());
        return dto;
    }
} 