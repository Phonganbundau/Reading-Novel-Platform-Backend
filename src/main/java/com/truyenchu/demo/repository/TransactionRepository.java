package com.truyenchu.demo.repository;

import com.truyenchu.demo.entity.Transaction;
import com.truyenchu.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Tìm tất cả transactions với phân trang
    Page<Transaction> findAll(Pageable pageable);
    
    // Tìm transactions theo status
    Page<Transaction> findByStatus(Transaction.TransactionStatus status, Pageable pageable);
    
    // Tìm transactions theo user
    Page<Transaction> findByUser(User user, Pageable pageable);
    
    // Tìm transactions theo user và status
    Page<Transaction> findByUserAndStatus(User user, Transaction.TransactionStatus status, Pageable pageable);
    
    // Tìm transactions gần đây nhất
    @Query("SELECT t FROM Transaction t ORDER BY t.createdAt DESC")
    Page<Transaction> findRecentTransactions(Pageable pageable);
    
    // Tìm transactions theo khoảng thời gian
    @Query("SELECT t FROM Transaction t WHERE t.createdAt BETWEEN :startDate AND :endDate ORDER BY t.createdAt DESC")
    Page<Transaction> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate, 
                                     Pageable pageable);
    
    // Đếm số transactions theo status
    long countByStatus(Transaction.TransactionStatus status);
    
    // Đếm số transactions theo user
    long countByUser(User user);
    
    // Tính tổng số tiền theo status
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") Transaction.TransactionStatus status);
    
    // Tính tổng số tiền theo user
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user = :user")
    BigDecimal sumAmountByUser(@Param("user") User user);
    
    // Tính tổng số tiền theo user và status
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user = :user AND t.status = :status")
    BigDecimal sumAmountByUserAndStatus(@Param("user") User user, @Param("status") Transaction.TransactionStatus status);
    
    // Thống kê transactions theo status
    @Query("SELECT t.status, COUNT(t) FROM Transaction t GROUP BY t.status")
    List<Object[]> countByStatusGroup();
    
    // Tìm transactions pending (chờ xử lý)
    @Query("SELECT t FROM Transaction t WHERE t.status = 'PENDING' ORDER BY t.createdAt ASC")
    Page<Transaction> findPendingTransactions(Pageable pageable);
    
    // Tìm transactions completed (đã hoàn thành)
    @Query("SELECT t FROM Transaction t WHERE t.status = 'COMPLETED' ORDER BY t.createdAt DESC")
    Page<Transaction> findCompletedTransactions(Pageable pageable);
    
    // Tìm transactions failed (thất bại)
    @Query("SELECT t FROM Transaction t WHERE t.status = 'FAILED' ORDER BY t.createdAt DESC")
    Page<Transaction> findFailedTransactions(Pageable pageable);
} 