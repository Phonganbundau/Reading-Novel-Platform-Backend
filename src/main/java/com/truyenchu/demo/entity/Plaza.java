package com.truyenchu.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "plazas")
public class Plaza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_plaza_id")
    private Plaza rootPlaza; // Plaza gốc (cấp 1)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_plaza_id")
    private Plaza parentPlaza; // Plaza cha (cấp trước)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người đăng bài

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // Nội dung bài đăng

    @Column(name = "likes_count")
    private Long likesCount = 0L; // Số lượt thích

    @Column(name = "replies_count")
    private Long repliesCount = 0L; // Số lượt trả lời

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private Story story; // Truyện được đề cập trong bài đăng

    @OneToMany(mappedBy = "parentPlaza", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Plaza> replies = new ArrayList<>(); // Danh sách các bài trả lời

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        // Tự động set rootPlaza
        if (parentPlaza != null) {
            if (parentPlaza.getRootPlaza() != null) {
                this.rootPlaza = parentPlaza.getRootPlaza();
            } else {
                this.rootPlaza = parentPlaza;
            }
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 