package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "post_seq")
    @SequenceGenerator(name = "post_seq",
            sequenceName = "post_seq",
            allocationSize = 1)
    private Long id;

    @Column(length = 2000, nullable = false)
    private String content;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostHashtag> postHashtags;

    public Post() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

	public boolean isPromotional() {
		return isPromotional;
	}

	public void setPromotional(boolean isPromotional) {
		this.isPromotional = isPromotional;
	}

	public String getCtaLink() {
		return ctaLink;
	}

	public void setCtaLink(String ctaLink) {
		this.ctaLink = ctaLink;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public boolean isPinned() {
		return isPinned;
	}

	public void setPinned(boolean isPinned) {
		this.isPinned = isPinned;
	}

	private boolean isPinned=false;
	
	@PrePersist
	protected void onCreate() {
		  this.createdAt=LocalDateTime.now();
	}
	

}

    public List<PostHashtag> getPostHashtags() { return postHashtags; }
    public void setPostHashtags(List<PostHashtag> postHashtags) {
        this.postHashtags = postHashtags;
    }
}

