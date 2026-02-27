package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_seq")
    @SequenceGenerator(
            name = "post_seq",
            sequenceName = "post_seq",
            allocationSize = 1
    )
    private Long id;

    // ================= BASIC =================

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ================= USER =================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ================= PIN =================

    @Column(name = "is_pinned", nullable = false)
    private Boolean pinned = false;

    @Column(name = "pinned_at")
    private LocalDateTime pinnedAt;

    // ================= SCHEDULE =================

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    // ================= HASHTAGS =================

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostHashtag> postHashtags = new ArrayList<>();

    // ================= ANALYTICS =================

    @OneToOne(mappedBy = "post", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private PostAnalytics analytics;

    // ================= RELATIONS =================

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostView> views = new ArrayList<>();

    // ================= AUTO TIMESTAMP =================

    @PrePersist
    protected void onCreate() {
        if (createdAt == null)
            createdAt = LocalDateTime.now();
    }

    // ================= GETTERS & SETTERS =================

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getPinned() {
        return pinned;
    }

    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }

    public LocalDateTime getPinnedAt() {
        return pinnedAt;
    }

    public void setPinnedAt(LocalDateTime pinnedAt) {
        this.pinnedAt = pinnedAt;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public List<PostHashtag> getPostHashtags() {
        return postHashtags;
    }

    public void setPostHashtags(List<PostHashtag> postHashtags) {
        this.postHashtags = postHashtags;
    }

    public PostAnalytics getAnalytics() {
        return analytics;
    }

    public void setAnalytics(PostAnalytics analytics) {
        this.analytics = analytics;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<PostView> getViews() {
        return views;
    }

    public void setViews(List<PostView> views) {
        this.views = views;
    }
}