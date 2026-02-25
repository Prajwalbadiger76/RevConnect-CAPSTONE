package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "POSTS")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_seq")
    @SequenceGenerator(name = "post_seq", sequenceName = "post_id_seq", allocationSize = 1)
    private Long id;

<<<<<<< Updated upstream
    @Column(nullable = false, length = 2000)
=======
    @Column(name = "CONTENT", nullable = false, length = 2000)
>>>>>>> Stashed changes
    private String content;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
    private LocalDateTime scheduledTime;
    private String ctaLink;

<<<<<<< Updated upstream
    private boolean isPinned = false;
    private boolean isPromotional = false;
    private boolean isRepost = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
=======
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
>>>>>>> Stashed changes
    private User user;

    @ManyToOne
    @JoinColumn(name = "original_post_id")
    private Post originalPost;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostHashtag> postHashtags;

<<<<<<< Updated upstream
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public LocalDateTime getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(LocalDateTime scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public String getCtaLink() {
		return ctaLink;
	}

	public void setCtaLink(String ctaLink) {
		this.ctaLink = ctaLink;
	}

	public boolean isPinned() {
		return isPinned;
	}

	public void setPinned(boolean isPinned) {
		this.isPinned = isPinned;
	}

	public boolean isPromotional() {
		return isPromotional;
	}

	public void setPromotional(boolean isPromotional) {
		this.isPromotional = isPromotional;
	}

	public boolean isRepost() {
		return isRepost;
	}

	public void setRepost(boolean isRepost) {
		this.isRepost = isRepost;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Post getOriginalPost() {
		return originalPost;
	}

	public void setOriginalPost(Post originalPost) {
		this.originalPost = originalPost;
	}

	public List<PostHashtag> getPostHashtags() {
		return postHashtags;
	}

	public void setPostHashtags(List<PostHashtag> postHashtags) {
		this.postHashtags = postHashtags;
	}

    
=======
   
    @Column(name = "IS_PINNED", nullable = false)
    private Boolean pinned = false;

    
    @Column(name = "PINNED_AT")
    private LocalDateTime pinnedAt;

    public Post() {}

    // getters & setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<PostHashtag> getPostHashtags() { return postHashtags; }
    public void setPostHashtags(List<PostHashtag> postHashtags) {
        this.postHashtags = postHashtags;
    }

    public Boolean getPinned() { return pinned; }
    public void setPinned(Boolean pinned) { this.pinned = pinned; }

    public LocalDateTime getPinnedAt() { return pinnedAt; }
    public void setPinnedAt(LocalDateTime pinnedAt) {
        this.pinnedAt = pinnedAt;
    }
>>>>>>> Stashed changes
}