package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostDto {

	private Long id;
	private String content;
	private String username;
	private LocalDateTime createdAt;

	private long likeCount;
	private boolean likedByCurrentUser;

	private List<CommentDto> comments = new ArrayList<>();

	private long totalComments;
	private long totalShares;
	private long reachCount;
	private double engagementRate;

	public PostDto() {
		this.comments = new ArrayList<>();
	}

	public PostDto(Long id, String content, String username, LocalDateTime createdAt) {
		this.id = id;
		this.content = content;
		this.username = username;
		this.createdAt = createdAt;
		this.comments = new ArrayList<>();
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public long getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(long likeCount) {
		this.likeCount = likeCount;
	}

	public boolean isLikedByCurrentUser() {
		return likedByCurrentUser;
	}

	public void setLikedByCurrentUser(boolean likedByCurrentUser) {
		this.likedByCurrentUser = likedByCurrentUser;
	}

	public List<CommentDto> getComments() {
		if (comments == null) {
			comments = new ArrayList<>();
		}
		return comments;
	}

	public void setComments(List<CommentDto> comments) {
		this.comments = (comments == null) ? new ArrayList<>() : comments;
	}


	public long getTotalComments() {
		return totalComments;
	}

	public void setTotalComments(long totalComments) {
		this.totalComments = totalComments;
	}

	public long getTotalShares() {
		return totalShares;
	}

	public void setTotalShares(long totalShares) {
		this.totalShares = totalShares;
	}

	public long getReachCount() {
		return reachCount;
	}

	public void setReachCount(long reachCount) {
		this.reachCount = reachCount;
	}

	public double getEngagementRate() {
		return engagementRate;
	}

	public void setEngagementRate(double engagementRate) {
		this.engagementRate = engagementRate;
	}
}