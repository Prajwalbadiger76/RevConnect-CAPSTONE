package com.example.demo.events;

public class LikeCreatedEvent {

    private final Long postId;
    private final String senderUsername;
    private final String recipientUsername;

    public LikeCreatedEvent(Long postId, String senderUsername, String recipientUsername) {
        this.postId = postId;
        this.senderUsername = senderUsername;
        this.recipientUsername = recipientUsername;
    }

    public Long getPostId() {
        return postId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getRecipientUsername() {
        return recipientUsername;
    }
}