package com.example.demo.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.demo.entity.Like;
import com.example.demo.events.LikeCreatedEvent;

import jakarta.persistence.PostPersist;

@Component
public class LikeEntityListener {

    private static ApplicationEventPublisher publisher;

    @Autowired
    public void setPublisher(ApplicationEventPublisher publisher) {
        LikeEntityListener.publisher = publisher;
    }

//    @PostPersist
//    public void afterInsert(Like like) {
//        publisher.publishEvent(
//                new LikeCreatedEvent(like.getPostId(), like.getUserId())
//        );
//    }
}