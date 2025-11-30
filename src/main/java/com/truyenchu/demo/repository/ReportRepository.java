package com.truyenchu.demo.repository;

import com.truyenchu.demo.entity.Chapter;
import com.truyenchu.demo.entity.Report;
import com.truyenchu.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    // Tìm tất cả reports với phân trang
    Page<Report> findAll(Pageable pageable);
    
    // Tìm reports theo status
    Page<Report> findByStatus(Report.ReportStatus status, Pageable pageable);
    
    // Tìm reports theo type
    Page<Report> findByType(Report.ReportType type, Pageable pageable);
    
    // Tìm reports theo user
    Page<Report> findByUserReport(User userReport, Pageable pageable);
    
    // Tìm reports theo story
    Page<Report> findByStoryId(Long storyId, Pageable pageable);
    
    // Tìm reports theo chapter
    Page<Report> findByChapter(Chapter chapter, Pageable pageable);
    
    // Tìm reports theo story và status
    Page<Report> findByStoryIdAndStatus(Long storyId, Report.ReportStatus status, Pageable pageable);
    
    // Tìm reports theo chapter và status
    Page<Report> findByChapterAndStatus(Chapter chapter, Report.ReportStatus status, Pageable pageable);
    
    // Đếm số reports theo status
    long countByStatus(Report.ReportStatus status);
    
    // Đếm số reports theo type
    long countByType(Report.ReportType type);
    
    // Đếm số reports theo story
    long countByStoryId(Long storyId);
    
    // Đếm số reports theo chapter
    long countByChapter(Chapter chapter);
    
    // Tìm reports theo user và story
    List<Report> findByUserReportAndStoryId(User userReport, Long storyId);
    
    // Tìm reports theo user và chapter
    List<Report> findByUserReportAndChapter(User userReport, Chapter chapter);
    
    // Tìm reports theo story và type
    Page<Report> findByStoryIdAndType(Long storyId, Report.ReportType type, Pageable pageable);
    
    // Tìm reports theo chapter và type
    Page<Report> findByChapterAndType(Chapter chapter, Report.ReportType type, Pageable pageable);
    
    // Thống kê reports theo status
    @Query("SELECT r.status, COUNT(r) FROM Report r GROUP BY r.status")
    List<Object[]> countByStatusGroup();
    
    // Thống kê reports theo type
    @Query("SELECT r.type, COUNT(r) FROM Report r GROUP BY r.type")
    List<Object[]> countByTypeGroup();
    
    // Tìm reports gần đây nhất
    @Query("SELECT r FROM Report r ORDER BY r.createdAt DESC")
    Page<Report> findRecentReports(Pageable pageable);
    
    // Tìm reports theo khoảng thời gian
    @Query("SELECT r FROM Report r WHERE r.createdAt BETWEEN :startDate AND :endDate ORDER BY r.createdAt DESC")
    Page<Report> findByDateRange(@Param("startDate") java.time.LocalDateTime startDate, 
                                 @Param("endDate") java.time.LocalDateTime endDate, 
                                 Pageable pageable);
} 