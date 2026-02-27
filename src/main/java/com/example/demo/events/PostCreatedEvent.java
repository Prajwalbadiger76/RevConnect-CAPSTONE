package com.example.demo.events;

public class PostCreatedEvent {

    private final Long postId;

    public PostCreatedEvent(Long postId) {
        this.postId = postId;
    }

    public Long getPostId() {
        return postId;
    }
}