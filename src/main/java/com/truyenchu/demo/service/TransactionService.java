package com.truyenchu.demo.service;

import com.truyenchu.demo.dto.CreateTransactionDTO;
import com.truyenchu.demo.dto.TransactionDTO;
import com.truyenchu.demo.dto.UpdateTransactionStatusDTO;
import com.truyenchu.demo.entity.Transaction;
import com.truyenchu.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public interface TransactionService {
    
    // Tạo yêu cầu rút tiền
    TransactionDTO createTransaction(User user, CreateTransactionDTO createTransactionDTO);
    
    // Lấy danh sách transactions với phân trang
    Page<TransactionDTO> getAllTransactions(Pageable pageable);
    
    // Lấy danh sách transactions theo status
    Page<TransactionDTO> getTransactionsByStatus(Transaction.TransactionStatus status, Pageable pageable);
    
    // Lấy danh sách transactions của user
    Page<TransactionDTO> getUserTransactions(User user, Pageable pageable);
    
    // Lấy danh sách transactions của user theo status
    Page<TransactionDTO> getUserTransactionsByStatus(User user, Transaction.TransactionStatus status, Pageable pageable);
    
    // Lấy transaction theo ID
    TransactionDTO getTransactionById(Long id);
    
    // Cập nhật trạng thái transaction (chỉ admin)
    TransactionDTO updateTransactionStatus(Long id, UpdateTransactionStatusDTO updateDTO);
    
    // Lấy thống kê transactions
    Map<String, Object> getTransactionStats();
    
    // Lấy thống kê transactions theo user
    Map<String, Object> getUserTransactionStats(User user);
    
    // Lấy danh sách transactions theo khoảng thời gian
    Page<TransactionDTO> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    // Lấy danh sách transactions pending (chờ xử lý)
    Page<TransactionDTO> getPendingTransactions(Pageable pageable);
    
    // Lấy danh sách transactions completed (đã hoàn thành)
    Page<TransactionDTO> getCompletedTransactions(Pageable pageable);
    
    // Lấy danh sách transactions failed (thất bại)
    Page<TransactionDTO> getFailedTransactions(Pageable pageable);
    
    // Kiểm tra xem user có thể tạo yêu cầu rút tiền không
    boolean canUserCreateTransaction(User user, BigDecimal amount);
    
    // Tính tổng số tiền đã rút của user
    BigDecimal getUserTotalWithdrawn(User user);
    
    // Tính tổng số tiền đang chờ xử lý của user
    BigDecimal getUserPendingAmount(User user);
} 