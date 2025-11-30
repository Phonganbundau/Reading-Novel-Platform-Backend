package com.truyenchu.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chapters")
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer chapterNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    @JsonBackReference
    private Story story;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReadingHistory> readingHistories = new ArrayList<>();

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChapterUnlock> chapterUnlocks = new ArrayList<>();

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();

    @Column(nullable = false)
    private boolean locked = false;

    @Column(nullable = false)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods để quản lý ReadingHistory
    public void addReadingHistory(ReadingHistory readingHistory) {
        readingHistories.add(readingHistory);
        readingHistory.setChapter(this);
    }

    public void removeReadingHistory(ReadingHistory readingHistory) {
        readingHistories.remove(readingHistory);
        readingHistory.setChapter(null);
    }

    // Helper methods để quản lý ChapterUnlock
    public void addChapterUnlock(ChapterUnlock chapterUnlock) {
        chapterUnlocks.add(chapterUnlock);
        chapterUnlock.setChapter(this);
    }

    public void removeChapterUnlock(ChapterUnlock chapterUnlock) {
        chapterUnlocks.remove(chapterUnlock);
        chapterUnlock.setChapter(null);
    }

    // Helper methods để quản lý Report
    public void addReport(Report report) {
        reports.add(report);
        report.setChapter(this);
    }

    public void removeReport(Report report) {
        reports.remove(report);
        report.setChapter(null);
    }
} 