package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PostDto {

    private Long id;
    private String content;
    private List<String> hashtags;
    private boolean isPromotional;
    private boolean isPinned;
    private boolean isRepost;
    private String ctaLink;
    private LocalDateTime scheduledTime;
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
	public List<String> getHashtags() {
		return hashtags;
	}
	public void setHashtags(List<String> hashtags) {
		this.hashtags = hashtags;
	}
	public boolean isPromotional() {
		return isPromotional;
	}
	public void setPromotional(boolean isPromotional) {
		this.isPromotional = isPromotional;
	}
	public boolean isPinned() {
		return isPinned;
	}
	public void setPinned(boolean isPinned) {
		this.isPinned = isPinned;
	}
	public boolean isRepost() {
		return isRepost;
	}
	public void setRepost(boolean isRepost) {
		this.isRepost = isRepost;
	}
	public String getCtaLink() {
		return ctaLink;
	}
	public void setCtaLink(String ctaLink) {
		this.ctaLink = ctaLink;
	}
	public LocalDateTime getScheduledTime() {
		return scheduledTime;
	}
	public void setScheduledTime(LocalDateTime scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

    
}