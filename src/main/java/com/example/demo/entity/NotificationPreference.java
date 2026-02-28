package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notification_preferences")
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private boolean likeEnabled = true;
    private boolean commentEnabled = true;
    private boolean followEnabled = true;
    private boolean connectionEnabled = true;
    
    // getters & setters
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public boolean isLikeEnabled() {
		return likeEnabled;
	}
	public void setLikeEnabled(boolean likeEnabled) {
		this.likeEnabled = likeEnabled;
	}
	public boolean isCommentEnabled() {
		return commentEnabled;
	}
	public void setCommentEnabled(boolean commentEnabled) {
		this.commentEnabled = commentEnabled;
	}
	public boolean isFollowEnabled() {
		return followEnabled;
	}
	public void setFollowEnabled(boolean followEnabled) {
		this.followEnabled = followEnabled;
	}
	public boolean isConnectionEnabled() {
		return connectionEnabled;
	}
	public void setConnectionEnabled(boolean connectionEnabled) {
		this.connectionEnabled = connectionEnabled;
	}

    // getters & setters
    
    
}