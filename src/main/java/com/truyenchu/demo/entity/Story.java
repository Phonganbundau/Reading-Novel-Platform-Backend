package com.truyenchu.demo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stories")
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 5000)
    private String description;

    @Column(name = "cover_url")
    private String coverUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    @JsonManagedReference
    private Genre genre;

    @ManyToMany
    @JoinTable(
        name = "story_tags",
        joinColumns = @JoinColumn(name = "story_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private StoryStatus status;

    @Column(name = "is_vip")
    private boolean isVip = false;

    @Column(name = "is_free")
    private boolean isFree = true;

    private String author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "translator_id")
    private User translator;

    @Column(name = "chapter_count")
    private Integer chapterCount = 0;

    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "vote_count")
    private Long voteCount = 0L;

    @Column(name = "follow_count")
    private Long followCount = 0L;

    @Column(name = "like_count")
    private Long likeCount = 0L;

    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Chapter> chapters = new ArrayList<>();

    // Cascade relationships for related entities
    @OneToMany(mappedBy = "story", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReadingHistory> readingHistories = new ArrayList<>();

    @OneToMany(mappedBy = "story", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<GiftTransaction> giftTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "story", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "story", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<StoryVote> storyVotes = new ArrayList<>();

    @OneToMany(mappedBy = "story", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "story", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Follow> follows = new ArrayList<>();

    @OneToMany(mappedBy = "story", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<EditorPick> editorPicks = new ArrayList<>();

    @OneToMany(mappedBy = "story", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "story", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UserBook> userBooks = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum StoryStatus {
        ONGOING, COMPLETED, DROPPED
    }
} 