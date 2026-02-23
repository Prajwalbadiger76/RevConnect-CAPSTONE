package com.example.demo.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.demo.entity.Post;
import com.example.demo.events.PostCreatedEvent;

import jakarta.persistence.PostPersist;

@Component
public class PostEntityListener {

    private static ApplicationEventPublisher publisher;

    @Autowired
    public void setPublisher(ApplicationEventPublisher publisher) {
        PostEntityListener.publisher = publisher;
    }

    @PostPersist
    public void afterInsert(Post post) {
        publisher.publishEvent(
                new PostCreatedEvent(post.getId(), post.getUserId())
        );
    }
}