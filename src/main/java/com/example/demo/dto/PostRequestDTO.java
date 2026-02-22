package com.example.demo.dto;

import java.time.LocalDateTime;


public class PostRequestDTO {
	private String content;
	private String hashtags;
	private boolean isPromotional;
	private String ctaLink;
	private LocalDateTime scheduledTime;
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
	public LocalDateTime getScheduledTime() {
		return scheduledTime;
	}
	public void setScheduledTime(LocalDateTime scheduledTime) {
		this.scheduledTime = scheduledTime;
	}
	
}
