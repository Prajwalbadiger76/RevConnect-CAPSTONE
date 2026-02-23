package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "post_analytics")
public class PostAnalytics {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false, unique = true)

	private Posts posts;

	private long totalLikes;
	private long totalComments;
	private long totalShares;
	private long reachCount;

	private LocalDateTime updatedAt;

	// getters & setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	
	public Posts getPost() {
		return posts;
	}

	public void setPost(Posts posts) {
		this.posts = posts;
	}

	public long getTotalLikes() {
		return totalLikes;
	}

	public void setTotalLikes(long totalLikes) {
		this.totalLikes = totalLikes;
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

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

}
