package com.example.demo.events;

public class LikeCreatedEvent {

<<<<<<< Updated upstream
	private final Long postId;
    private final Long userId;

    public LikeCreatedEvent(Long postId, Long userId) {
        this.postId = postId;
        this.userId = userId;
    }

    public Long getPostId() { return postId; }
    public Long getUserId() { return userId; }
}
=======
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
>>>>>>> Stashed changes
