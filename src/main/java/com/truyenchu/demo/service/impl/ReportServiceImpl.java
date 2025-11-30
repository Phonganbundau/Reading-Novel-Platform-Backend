package com.truyenchu.demo.service.impl;

import com.truyenchu.demo.dto.CreateReportDTO;
import com.truyenchu.demo.dto.ReportDTO;
import com.truyenchu.demo.dto.UpdateReportStatusDTO;
import com.truyenchu.demo.entity.Chapter;
import com.truyenchu.demo.entity.Report;
import com.truyenchu.demo.entity.Story;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.exception.ResourceNotFoundException;
import com.truyenchu.demo.exception.UnauthorizedException;
import com.truyenchu.demo.repository.ChapterRepository;
import com.truyenchu.demo.repository.ReportRepository;
import com.truyenchu.demo.repository.StoryRepository;
import com.truyenchu.demo.repository.UserRepository;
import com.truyenchu.demo.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {
    
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;
    private final StoryRepository storyRepository;
    
    @Override
    @Transactional
    public ReportDTO createReport(CreateReportDTO createReportDTO, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Kiểm tra xem user đã report story/chapter này chưa
        if (createReportDTO.getStoryId() != null) {
            if (hasUserReportedStory(username, createReportDTO.getStoryId())) {
                throw new RuntimeException("You have already reported this story");
            }
        }
        
        if (createReportDTO.getChapterNumber() != null) {
            if (hasUserReportedChapter(username, createReportDTO.getStoryId(), createReportDTO.getChapterNumber())) {
                throw new RuntimeException("You have already reported this chapter");
            }
        }
        
        Report report = new Report();
        report.setType(createReportDTO.getType());
        report.setStoryId(createReportDTO.getStoryId());
        if (createReportDTO.getChapterNumber() != null) {
            Story story = storyRepository.findById(createReportDTO.getStoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Story not found"));
            Chapter chapter = chapterRepository.findByStoryAndChapterNumber(story, createReportDTO.getChapterNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));
            report.setChapter(chapter);
        }
        report.setContent(createReportDTO.getContent());
        report.setUserReport(user);
        report.setStatus(Report.ReportStatus.PENDING);
        
        Report savedReport = reportRepository.save(report);
        return ReportDTO.fromEntity(savedReport);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ReportDTO getReportById(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        return ReportDTO.fromEntity(report);
    }
    
    @Override
    @Transactional
    public ReportDTO updateReportStatus(Long id, UpdateReportStatusDTO updateDTO) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        
        report.setStatus(updateDTO.getStatus());
        
        Report savedReport = reportRepository.save(report);
        return ReportDTO.fromEntity(savedReport);
    }
    
    @Override
    @Transactional
    public void deleteReport(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        reportRepository.delete(report);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ReportDTO> getAllReports(Pageable pageable) {
        return reportRepository.findAll(pageable)
                .map(ReportDTO::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ReportDTO> getReportsByStatus(Report.ReportStatus status, Pageable pageable) {
        return reportRepository.findByStatus(status, pageable)
                .map(ReportDTO::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ReportDTO> getReportsByType(Report.ReportType type, Pageable pageable) {
        return reportRepository.findByType(type, pageable)
                .map(ReportDTO::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ReportDTO> getReportsByUser(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return reportRepository.findByUserReport(user, pageable)
                .map(ReportDTO::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ReportDTO> getReportsByStory(Long storyId, Pageable pageable) {
        return reportRepository.findByStoryId(storyId, pageable)
                .map(ReportDTO::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ReportDTO> getReportsByChapter(Long storyId, Integer chapterNumber, Pageable pageable) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found"));
        Chapter chapter = chapterRepository.findByStoryAndChapterNumber(story, chapterNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));
        return reportRepository.findByChapter(chapter, pageable)
                .map(ReportDTO::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ReportDTO> getRecentReports(Pageable pageable) {
        return reportRepository.findRecentReports(pageable)
                .map(ReportDTO::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ReportDTO> getReportsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return reportRepository.findByDateRange(startDate, endDate, pageable)
                .map(ReportDTO::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getReportStatsByStatus() {
        List<Object[]> results = reportRepository.countByStatusGroup();
        Map<String, Long> stats = new HashMap<>();
        
        for (Object[] result : results) {
            Report.ReportStatus status = (Report.ReportStatus) result[0];
            Long count = ((Number) result[1]).longValue();
            stats.put(status.getDisplayName(), count);
        }
        
        return stats;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getReportStatsByType() {
        List<Object[]> results = reportRepository.countByTypeGroup();
        Map<String, Long> stats = new HashMap<>();
        
        for (Object[] result : results) {
            Report.ReportType type = (Report.ReportType) result[0];
            Long count = ((Number) result[1]).longValue();
            stats.put(type.getDisplayName(), count);
        }
        
        return stats;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReportedStory(String username, Long storyId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Report> reports = reportRepository.findByUserReportAndStoryId(user, storyId);
        return !reports.isEmpty();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReportedChapter(String username, Long storyId, Integer chapterNumber) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found"));
        Chapter chapter = chapterRepository.findByStoryAndChapterNumber(story, chapterNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));
        List<Report> reports = reportRepository.findByUserReportAndChapter(user, chapter);
        return !reports.isEmpty();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getReportCountByStory(Long storyId) {
        return reportRepository.countByStoryId(storyId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getReportCountByChapter(Long storyId, Integer chapterNumber) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found"));
        Chapter chapter = chapterRepository.findByStoryAndChapterNumber(story, chapterNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));
        return reportRepository.countByChapter(chapter);
    }
} 