package com.example.demo.events;

public class ConnectionEvent {
	private final Long senderId;
    private final Long receiverId;
    private final String status;

    public ConnectionEvent(Long senderId, Long receiverId, String status) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = status;
    }

    public Long getSenderId() { return senderId; }
    public Long getReceiverId() { return receiverId; }
    public String getStatus() { return status; }
}
