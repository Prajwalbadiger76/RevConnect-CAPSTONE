package com.example.demo.events;

public class LikeCreatedEvent {

	private final Long postId;
    private final Long userId;

    public LikeCreatedEvent(Long postId, Long userId) {
        this.postId = postId;
        this.userId = userId;
    }

    public Long getPostId() { return postId; }
    public Long getUserId() { return userId; }
}
