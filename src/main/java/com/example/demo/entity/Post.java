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

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "pinned_at")
    private LocalDateTime pinnedAt;

    @Column(name = "is_pinned", nullable = false)
    private Boolean pinned = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostHashtag> postHashtags = new ArrayList<>();
    
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    // Getters & Setters

    public Long getId() { return id; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getPinnedAt() { return pinnedAt; }

    public void setPinnedAt(LocalDateTime pinnedAt) { this.pinnedAt = pinnedAt; }

    public Boolean getPinned() { return pinned; }

    public void setPinned(Boolean pinned) { this.pinned = pinned; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public List<PostHashtag> getPostHashtags() { return postHashtags; }

    public void setPostHashtags(List<PostHashtag> postHashtags) { this.postHashtags = postHashtags; }

	public LocalDateTime getScheduledAt() {
		return scheduledAt;
	}

	public void setScheduledAt(LocalDateTime scheduledAt) {
		this.scheduledAt = scheduledAt;
	}
    
    
    
    
}