package com.truyenchu.demo.service.impl;

import com.truyenchu.demo.dto.CreateTransactionDTO;
import com.truyenchu.demo.dto.TransactionDTO;
import com.truyenchu.demo.dto.UpdateTransactionStatusDTO;
import com.truyenchu.demo.entity.Transaction;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.exception.BusinessException;
import com.truyenchu.demo.exception.ResourceNotFoundException;
import com.truyenchu.demo.repository.TransactionRepository;
import com.truyenchu.demo.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public TransactionDTO createTransaction(User user, CreateTransactionDTO createTransactionDTO) {
        // Kiểm tra xem user có thể tạo yêu cầu rút tiền không
        if (!canUserCreateTransaction(user, createTransactionDTO.getAmount())) {
            throw new BusinessException("Insufficient balance or invalid amount for withdrawal");
        }

        // Tạo transaction mới
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(createTransactionDTO.getAmount());
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        transaction.setNote(createTransactionDTO.getNote());
        
        // Lưu transaction
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        return TransactionDTO.fromEntity(savedTransaction);
    }

    @Override
    public Page<TransactionDTO> getAllTransactions(Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findAll(pageable);
        return transactions.map(TransactionDTO::fromEntity);
    }

    @Override
    public Page<TransactionDTO> getTransactionsByStatus(Transaction.TransactionStatus status, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByStatus(status, pageable);
        return transactions.map(TransactionDTO::fromEntity);
    }

    @Override
    public Page<TransactionDTO> getUserTransactions(User user, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByUser(user, pageable);
        return transactions.map(TransactionDTO::fromEntity);
    }

    @Override
    public Page<TransactionDTO> getUserTransactionsByStatus(User user, Transaction.TransactionStatus status, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByUserAndStatus(user, status, pageable);
        return transactions.map(TransactionDTO::fromEntity);
    }

    @Override
    public TransactionDTO getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        return TransactionDTO.fromEntity(transaction);
    }

    @Override
    public TransactionDTO updateTransactionStatus(Long id, UpdateTransactionStatusDTO updateDTO) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        // Cập nhật status và adminNote
        transaction.setStatus(updateDTO.getStatus());
        if (updateDTO.getAdminNote() != null) {
            transaction.setAdminNote(updateDTO.getAdminNote());
        }
        
        // Lưu transaction
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        return TransactionDTO.fromEntity(savedTransaction);
    }

    @Override
    public Map<String, Object> getTransactionStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Đếm số transactions theo status
        long pendingCount = transactionRepository.countByStatus(Transaction.TransactionStatus.PENDING);
        long completedCount = transactionRepository.countByStatus(Transaction.TransactionStatus.COMPLETED);
        long failedCount = transactionRepository.countByStatus(Transaction.TransactionStatus.FAILED);
        
        // Tính tổng số tiền theo status
        BigDecimal pendingAmount = transactionRepository.sumAmountByStatus(Transaction.TransactionStatus.PENDING);
        BigDecimal completedAmount = transactionRepository.sumAmountByStatus(Transaction.TransactionStatus.COMPLETED);
        BigDecimal failedAmount = transactionRepository.sumAmountByStatus(Transaction.TransactionStatus.FAILED);
        
        stats.put("pendingCount", pendingCount);
        stats.put("completedCount", completedCount);
        stats.put("failedCount", failedCount);
        stats.put("pendingAmount", pendingAmount);
        stats.put("completedAmount", completedAmount);
        stats.put("failedAmount", failedAmount);
        stats.put("totalCount", pendingCount + completedCount + failedCount);
        stats.put("totalAmount", pendingAmount.add(completedAmount).add(failedAmount));
        
        return stats;
    }

    @Override
    public Map<String, Object> getUserTransactionStats(User user) {
        Map<String, Object> stats = new HashMap<>();
        
        // Đếm số transactions của user
        long totalCount = transactionRepository.countByUser(user);
        
        // Tính tổng số tiền theo status
        BigDecimal pendingAmount = transactionRepository.sumAmountByUserAndStatus(user, Transaction.TransactionStatus.PENDING);
        BigDecimal completedAmount = transactionRepository.sumAmountByUserAndStatus(user, Transaction.TransactionStatus.COMPLETED);
        BigDecimal failedAmount = transactionRepository.sumAmountByUserAndStatus(user, Transaction.TransactionStatus.FAILED);
        
        stats.put("totalCount", totalCount);
        stats.put("pendingAmount", pendingAmount);
        stats.put("completedAmount", completedAmount);
        stats.put("failedAmount", failedAmount);
        stats.put("totalAmount", pendingAmount.add(completedAmount).add(failedAmount));
        
        return stats;
    }

    @Override
    public Page<TransactionDTO> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByDateRange(startDate, endDate, pageable);
        return transactions.map(TransactionDTO::fromEntity);
    }

    @Override
    public Page<TransactionDTO> getPendingTransactions(Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findPendingTransactions(pageable);
        return transactions.map(TransactionDTO::fromEntity);
    }

    @Override
    public Page<TransactionDTO> getCompletedTransactions(Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findCompletedTransactions(pageable);
        return transactions.map(TransactionDTO::fromEntity);
    }

    @Override
    public Page<TransactionDTO> getFailedTransactions(Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findFailedTransactions(pageable);
        return transactions.map(TransactionDTO::fromEntity);
    }

    @Override
    public boolean canUserCreateTransaction(User user, BigDecimal amount) {
        // Kiểm tra số dư của user (giả sử có field balance trong User entity)
        // Nếu không có field balance, có thể tính từ các transactions khác
        BigDecimal userBalance = getUserBalance(user);
        BigDecimal pendingAmount = getUserPendingAmount(user);
        
        // Số dư phải đủ để rút + số tiền đang chờ xử lý
        return userBalance.compareTo(amount.add(pendingAmount)) >= 0 && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public BigDecimal getUserTotalWithdrawn(User user) {
        return transactionRepository.sumAmountByUserAndStatus(user, Transaction.TransactionStatus.COMPLETED);
    }

    @Override
    public BigDecimal getUserPendingAmount(User user) {
        return transactionRepository.sumAmountByUserAndStatus(user, Transaction.TransactionStatus.PENDING);
    }

    // Helper method để tính số dư của user
    private BigDecimal getUserBalance(User user) {
        // Sử dụng coinBalance từ User entity
        return user.getCoinBalance() != null ? user.getCoinBalance() : BigDecimal.ZERO;
    }
} 