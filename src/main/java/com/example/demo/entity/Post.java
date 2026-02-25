package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_seq")
    @SequenceGenerator(name = "post_seq", sequenceName = "post_seq", allocationSize = 1)
    private Long id;

    @Column(length = 2000, nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Post owner
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Hashtags relation
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostHashtag> postHashtags;

    // Optional social flags
    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false;

    @Column(name = "is_promotional", nullable = false)
    private Boolean isPromotional = false;

    public Post() {}

    // Automatically set timestamp when saved
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // -------- Getters & Setters --------

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<PostHashtag> getPostHashtags() {
        return postHashtags;
    }

    public void setPostHashtags(List<PostHashtag> postHashtags) {
        this.postHashtags = postHashtags;
    }

    public Boolean getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(Boolean pinned) {
        isPinned = pinned;
    }

    public Boolean getIsPromotional() {
        return isPromotional;
    }

    public void setIsPromotional(Boolean promotional) {
        isPromotional = promotional;
    }
}