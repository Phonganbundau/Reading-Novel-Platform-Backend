package com.truyenchu.demo.controller;

import com.truyenchu.demo.dto.CreateReportDTO;
import com.truyenchu.demo.dto.ReportDTO;
import com.truyenchu.demo.dto.UpdateReportStatusDTO;
import com.truyenchu.demo.entity.Report;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.service.ReportService;
import com.truyenchu.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    
    private final ReportService reportService;
    private final UserService userService;
    
    // Tạo report mới (cho user)
    @PostMapping
    public ResponseEntity<ReportDTO> createReport(@Valid @RequestBody CreateReportDTO createReportDTO, 
                                                 Authentication authentication) {
        ReportDTO report = reportService.createReport(createReportDTO, authentication.getName());
        return ResponseEntity.ok(report);
    }
    
    // Lấy report theo ID (admin)
    @GetMapping("/{id}")
    public ResponseEntity<ReportDTO> getReportById(@PathVariable Long id) {
        ReportDTO report = reportService.getReportById(id);
        return ResponseEntity.ok(report);
    }
    
    // Cập nhật status của report (admin)
    @PutMapping("/{id}/status")
    public ResponseEntity<ReportDTO> updateReportStatus(@PathVariable Long id, 
                                                       @Valid @RequestBody UpdateReportStatusDTO updateDTO) {
        ReportDTO report = reportService.updateReportStatus(id, updateDTO);
        return ResponseEntity.ok(report);
    }
    
    // Xóa report (admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.ok().build();
    }
    
    // Lấy tất cả reports với phân trang (admin)
    @GetMapping
    public ResponseEntity<Page<ReportDTO>> getAllReports(Pageable pageable) {
        Page<ReportDTO> reports = reportService.getAllReports(pageable);
        return ResponseEntity.ok(reports);
    }
    
    // Lấy reports theo status (admin)
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<ReportDTO>> getReportsByStatus(@PathVariable Report.ReportStatus status, 
                                                             Pageable pageable) {
        Page<ReportDTO> reports = reportService.getReportsByStatus(status, pageable);
        return ResponseEntity.ok(reports);
    }
    
    // Lấy reports theo type (admin)
    @GetMapping("/type/{type}")
    public ResponseEntity<Page<ReportDTO>> getReportsByType(@PathVariable Report.ReportType type, 
                                                           Pageable pageable) {
        Page<ReportDTO> reports = reportService.getReportsByType(type, pageable);
        return ResponseEntity.ok(reports);
    }
    
    // Lấy reports theo user (user chỉ xem được reports của mình, admin xem được tất cả)
    @GetMapping("/user/{username}")
    public ResponseEntity<Page<ReportDTO>> getReportsByUser(@PathVariable String username, 
                                                           Authentication authentication,
                                                           Pageable pageable) {
        // Kiểm tra quyền: user chỉ có thể xem reports của mình, admin có thể xem tất cả
        String currentUsername = authentication.getName();
        User currentUser = userService.findByUsername(currentUsername);
        
        // Nếu không phải admin và không phải xem reports của chính mình
        if (!currentUser.getRole().equals(User.UserRole.ADMIN) && !currentUsername.equals(username)) {
            return ResponseEntity.status(403).body(null);
        }
        
        Page<ReportDTO> reports = reportService.getReportsByUser(username, pageable);
        return ResponseEntity.ok(reports);
    }
    
    // Lấy reports theo story (admin)
    @GetMapping("/story/{storyId}")
    public ResponseEntity<Page<ReportDTO>> getReportsByStory(@PathVariable Long storyId, 
                                                            Pageable pageable) {
        Page<ReportDTO> reports = reportService.getReportsByStory(storyId, pageable);
        return ResponseEntity.ok(reports);
    }
    
    // Lấy reports theo chapter (admin)
    @GetMapping("/chapter/{storyId}/{chapterNumber}")
    public ResponseEntity<Page<ReportDTO>> getReportsByChapter(@PathVariable Long storyId, 
                                                              @PathVariable Integer chapterNumber,
                                                              Pageable pageable) {
        Page<ReportDTO> reports = reportService.getReportsByChapter(storyId, chapterNumber, pageable);
        return ResponseEntity.ok(reports);
    }
    
    // Lấy reports gần đây nhất (admin)
    @GetMapping("/recent")
    public ResponseEntity<Page<ReportDTO>> getRecentReports(Pageable pageable) {
        Page<ReportDTO> reports = reportService.getRecentReports(pageable);
        return ResponseEntity.ok(reports);
    }
    
    // Lấy reports theo khoảng thời gian (admin)
    @GetMapping("/date-range")
    public ResponseEntity<Page<ReportDTO>> getReportsByDateRange(@RequestParam String startDate, 
                                                                @RequestParam String endDate, 
                                                                Pageable pageable) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);
        
        Page<ReportDTO> reports = reportService.getReportsByDateRange(start, end, pageable);
        return ResponseEntity.ok(reports);
    }
    
    // Thống kê reports theo status (admin)
    @GetMapping("/stats/status")
    public ResponseEntity<Map<String, Long>> getReportStatsByStatus() {
        Map<String, Long> stats = reportService.getReportStatsByStatus();
        return ResponseEntity.ok(stats);
    }
    
    // Thống kê reports theo type (admin)
    @GetMapping("/stats/type")
    public ResponseEntity<Map<String, Long>> getReportStatsByType() {
        Map<String, Long> stats = reportService.getReportStatsByType();
        return ResponseEntity.ok(stats);
    }
    
    // Kiểm tra user đã report story chưa
    @GetMapping("/check/story/{storyId}")
    public ResponseEntity<Map<String, Boolean>> hasUserReportedStory(@PathVariable Long storyId, 
                                                                    Authentication authentication) {
        boolean hasReported = reportService.hasUserReportedStory(authentication.getName(), storyId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasReported", hasReported);
        return ResponseEntity.ok(response);
    }
    
    // Kiểm tra user đã report chapter chưa
    @GetMapping("/check/chapter/{storyId}/{chapterNumber}")
    public ResponseEntity<Map<String, Boolean>> hasUserReportedChapter(@PathVariable Long storyId, 
                                                                      @PathVariable Integer chapterNumber,
                                                                      Authentication authentication) {
        boolean hasReported = reportService.hasUserReportedChapter(authentication.getName(), storyId, chapterNumber);
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasReported", hasReported);
        return ResponseEntity.ok(response);
    }
    
    // Lấy số lượng reports theo story
    @GetMapping("/count/story/{storyId}")
    public ResponseEntity<Map<String, Long>> getReportCountByStory(@PathVariable Long storyId) {
        long count = reportService.getReportCountByStory(storyId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
    
    // Lấy số lượng reports theo chapter
    @GetMapping("/count/chapter/{storyId}/{chapterNumber}")
    public ResponseEntity<Map<String, Long>> getReportCountByChapter(@PathVariable Long storyId, 
                                                                    @PathVariable Integer chapterNumber) {
        long count = reportService.getReportCountByChapter(storyId, chapterNumber);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
    
    // Lấy danh sách các loại report
    @GetMapping("/types")
    public ResponseEntity<Report.ReportType[]> getReportTypes() {
        return ResponseEntity.ok(Report.ReportType.values());
    }
    
    // Lấy danh sách các status
    @GetMapping("/statuses")
    public ResponseEntity<Report.ReportStatus[]> getReportStatuses() {
        return ResponseEntity.ok(Report.ReportStatus.values());
    }
} 