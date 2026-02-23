package com.example.demo.events;

public class CommentCreatedEvent {

	 private final Long postId;
	    private final Long userId;

	    public CommentCreatedEvent(Long postId, Long userId) {
	        this.postId = postId;
	        this.userId = userId;
	    }

	    public Long getPostId() { return postId; }
	    public Long getUserId() { return userId; }
}
