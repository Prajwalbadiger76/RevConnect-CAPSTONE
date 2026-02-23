package com.example.demo.events;

public class PostCreatedEvent {
	
	private final Long postId;
    private final Long authorId;

    public PostCreatedEvent(Long postId, Long authorId) {
        this.postId = postId;
        this.authorId = authorId;
    }

    public Long getPostId() { return postId; }
    public Long getAuthorId() { return authorId; }

}
