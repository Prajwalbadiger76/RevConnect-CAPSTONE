package com.example.demo.service;

import org.springframework.context.ApplicationEventPublisher;
import com.example.demo.events.CommentCreatedEvent;
import org.springframework.stereotype.Service;

import com.example.demo.events.LikeCreatedEvent;

@Service
public class EventPublisherService {

	private final ApplicationEventPublisher publisher;

	public EventPublisherService(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	public void publishLikeCreated(Long postId, String senderUsername, String recipientUsername) {

		System.out.println("LIKE EVENT PUBLISHED");

		publisher.publishEvent(new LikeCreatedEvent(postId, senderUsername, recipientUsername));
	}

	public void publishCommentCreated(Long postId, String senderUsername, String recipientUsername) {

		System.out.println("COMMENT EVENT PUBLISHED");

		publisher.publishEvent(new CommentCreatedEvent(postId, senderUsername, recipientUsername));
	}
}