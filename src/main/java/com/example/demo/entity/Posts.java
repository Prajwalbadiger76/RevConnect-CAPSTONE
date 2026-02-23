package com.example.demo.entity;

import java.time.LocalDateTime;


import jakarta.persistence.*;



@Entity
@Table(name="posts")
public class Posts {

	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="post_seq")
	@SequenceGenerator(name="post_seq",sequenceName="POST_ID_SEQ",allocationSize=1)
	private Long id;
	
	@Column(nullable=false, length=2000)
	private String content;
	
	private String hashtags;
	
	private LocalDateTime createdAt;
	private LocalDateTime scheduledTime;
	
	private boolean isPromotional = false;
	private String ctaLink;
	
	@Column(nullable=false)
	private Long userId;
	
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

	public String getHashtags() {
		return hashtags;
	}

	public void setHashtags(String hashtags) {
		this.hashtags = hashtags;
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
