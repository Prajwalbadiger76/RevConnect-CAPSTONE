package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_analytics")
public class PostAnalytics {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_analytics_seq")
	@SequenceGenerator(name = "post_analytics_seq", sequenceName = "post_analytics_seq", allocationSize = 1)
	private Long id;

	// 🔴 IMPORTANT: property name MUST be 'post'
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false, unique = true)
	private Post post;

	private long totalLikes = 0;
	private long totalComments = 0;
	private long totalShares = 0;
	private long reachCount = 0;

	private LocalDateTime updatedAt = LocalDateTime.now();

	// ===== Getters & Setters =====

	public Long getId() {
		return id;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public long getTotalLikes() {
		return totalLikes;
	}

	public void setTotalLikes(long totalLikes) {
		this.totalLikes = totalLikes;
		this.updatedAt = LocalDateTime.now();
	}

	public long getTotalComments() {
		return totalComments;
	}

	public void setTotalComments(long totalComments) {
		this.totalComments = totalComments;
		this.updatedAt = LocalDateTime.now();
	}

	public long getTotalShares() {
		return totalShares;
	}

	public void setTotalShares(long totalShares) {
		this.totalShares = totalShares;
		this.updatedAt = LocalDateTime.now();
	}

	public long getReachCount() {
		return reachCount;
	}

	public void setReachCount(long reachCount) {
		this.reachCount = reachCount;
		this.updatedAt = LocalDateTime.now();
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}