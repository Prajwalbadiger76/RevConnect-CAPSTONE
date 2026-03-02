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
    private boolean promotional;

 

	// social media features
    private boolean pinned;
    private List<String> hashtags = new ArrayList<>();

    // comments
    private List<CommentDto> comments = new ArrayList<>();

    // analytics
    private long totalComments;
    private long totalShares;
    private long reachCount;
    private double engagementRate;
    

    public PostDto() {}

    public PostDto(Long id, String content, String username, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.username = username;
        this.createdAt = createdAt;
    }

    // ================= BASIC =================

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
    
    public boolean isPromotional() {
 		return promotional;
 	}

 	public void setPromotional(boolean promotional) {
 		this.promotional = promotional;
 	}

    // ================= LIKES =================

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
    
 // ================= RE-SHARE POST =================
    
    private boolean sharedByCurrentUser;

    public boolean isSharedByCurrentUser() {
        return sharedByCurrentUser;
    }

    public void setSharedByCurrentUser(boolean sharedByCurrentUser) {
        this.sharedByCurrentUser = sharedByCurrentUser;
    }
    
    private String sharedFromUsername;

    public String getSharedFromUsername() {
        return sharedFromUsername;
    }

    public void setSharedFromUsername(String sharedFromUsername) {
        this.sharedFromUsername = sharedFromUsername;
    }

    // ================= PIN =================

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    // ================= HASHTAGS =================

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = (hashtags == null) ? new ArrayList<>() : hashtags;
    }

    // ================= COMMENTS =================

    public List<CommentDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = (comments == null) ? new ArrayList<>() : comments;
    }

    // ================= ANALYTICS =================

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