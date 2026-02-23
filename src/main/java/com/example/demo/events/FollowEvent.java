package com.example.demo.events;

public class FollowEvent {
	private final Long followerId;
    private final Long followingId;

    public FollowEvent(Long followerId, Long followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }

    public Long getFollowerId() { return followerId; }
    public Long getFollowingId() { return followingId; }
}
