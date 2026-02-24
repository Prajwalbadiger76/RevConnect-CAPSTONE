package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

<<<<<<< Updated upstream
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

=======
>>>>>>> Stashed changes
@Entity
@Table(name = "post_analytics")
public class PostAnalytics {

<<<<<<< Updated upstream
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false, unique = true)

	private Post posts;

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

	
	
	
	public Post getPost() {
		return posts;
	}

	public void setPost(Post posts) {
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
=======
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_analytics_seq")
    @SequenceGenerator(
            name = "post_analytics_seq",
            sequenceName = "post_analytics_seq",
            allocationSize = 1
    )
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
>>>>>>> Stashed changes
