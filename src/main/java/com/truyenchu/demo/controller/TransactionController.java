package com.truyenchu.demo.controller;

import com.truyenchu.demo.dto.CreateTransactionDTO;
import com.truyenchu.demo.dto.TransactionDTO;
import com.truyenchu.demo.dto.UpdateTransactionStatusDTO;
import com.truyenchu.demo.entity.Transaction;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // Translator tạo yêu cầu rút tiền
    @PostMapping
    @PreAuthorize("hasRole('TRANSLATOR')")
    public ResponseEntity<TransactionDTO> createTransaction(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateTransactionDTO createTransactionDTO) {
        
        TransactionDTO transaction = transactionService.createTransaction(user, createTransactionDTO);
        return ResponseEntity.ok(transaction);
    }

    // Translator xem danh sách transactions của mình
    @GetMapping("/my")
    @PreAuthorize("hasRole('TRANSLATOR')")
    public ResponseEntity<Page<TransactionDTO>> getMyTransactions(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TransactionDTO> transactions = transactionService.getUserTransactions(user, pageable);
        return ResponseEntity.ok(transactions);
    }

    // Translator xem danh sách transactions của mình theo status
    @GetMapping("/my/status/{status}")
    @PreAuthorize("hasRole('TRANSLATOR')")
    public ResponseEntity<Page<TransactionDTO>> getMyTransactionsByStatus(
            @AuthenticationPrincipal User user,
            @PathVariable Transaction.TransactionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TransactionDTO> transactions = transactionService.getUserTransactionsByStatus(user, status, pageable);
        return ResponseEntity.ok(transactions);
    }

    // Translator xem thống kê transactions của mình
    @GetMapping("/my/stats")
    @PreAuthorize("hasRole('TRANSLATOR')")
    public ResponseEntity<Map<String, Object>> getMyTransactionStats(@AuthenticationPrincipal User user) {
        Map<String, Object> stats = transactionService.getUserTransactionStats(user);
        return ResponseEntity.ok(stats);
    }

    // Translator kiểm tra có thể tạo yêu cầu rút tiền không
    @GetMapping("/my/can-withdraw")
    @PreAuthorize("hasRole('TRANSLATOR')")
    public ResponseEntity<Map<String, Object>> canWithdraw(
            @AuthenticationPrincipal User user,
            @RequestParam BigDecimal amount) {
        
        boolean canWithdraw = transactionService.canUserCreateTransaction(user, amount);
        BigDecimal totalWithdrawn = transactionService.getUserTotalWithdrawn(user);
        BigDecimal pendingAmount = transactionService.getUserPendingAmount(user);
        
        Map<String, Object> response = Map.of(
            "canWithdraw", canWithdraw,
            "totalWithdrawn", totalWithdrawn,
            "pendingAmount", pendingAmount
        );
        
        return ResponseEntity.ok(response);
    }

    // Admin xem tất cả transactions
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TransactionDTO>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TransactionDTO> transactions = transactionService.getAllTransactions(pageable);
        return ResponseEntity.ok(transactions);
    }

    // Admin xem transactions theo status
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TransactionDTO>> getTransactionsByStatus(
            @PathVariable Transaction.TransactionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TransactionDTO> transactions = transactionService.getTransactionsByStatus(status, pageable);
        return ResponseEntity.ok(transactions);
    }

    // Admin xem transactions pending (chờ xử lý)
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TransactionDTO>> getPendingTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<TransactionDTO> transactions = transactionService.getPendingTransactions(pageable);
        return ResponseEntity.ok(transactions);
    }

    // Admin xem transactions completed
    @GetMapping("/completed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TransactionDTO>> getCompletedTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TransactionDTO> transactions = transactionService.getCompletedTransactions(pageable);
        return ResponseEntity.ok(transactions);
    }

    // Admin xem transactions failed
    @GetMapping("/failed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TransactionDTO>> getFailedTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TransactionDTO> transactions = transactionService.getFailedTransactions(pageable);
        return ResponseEntity.ok(transactions);
    }

    // Admin xem transaction theo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        TransactionDTO transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }

    // Admin cập nhật trạng thái transaction
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionDTO> updateTransactionStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTransactionStatusDTO updateDTO) {
        
        TransactionDTO transaction = transactionService.updateTransactionStatus(id, updateDTO);
        return ResponseEntity.ok(transaction);
    }

    // Admin xem thống kê transactions
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getTransactionStats() {
        Map<String, Object> stats = transactionService.getTransactionStats();
        return ResponseEntity.ok(stats);
    }

    // Admin xem transactions theo khoảng thời gian
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TransactionDTO>> getTransactionsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TransactionDTO> transactions = transactionService.getTransactionsByDateRange(start, end, pageable);
        return ResponseEntity.ok(transactions);
    }
} 