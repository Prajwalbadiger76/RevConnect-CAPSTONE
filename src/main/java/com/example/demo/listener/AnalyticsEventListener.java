package com.example.demo.listener;

import com.example.demo.events.LikeCreatedEvent;
import com.example.demo.events.CommentCreatedEvent;
import com.example.demo.service.AnalyticsService;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
public class AnalyticsEventListener {

    private final AnalyticsService analyticsService;

    public AnalyticsEventListener(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // 🔥 LIKE ANALYTICS
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLikeCreated(LikeCreatedEvent event) {

        System.out.println("🔥 ANALYTICS LIKE EVENT RECEIVED");

        analyticsService.incrementLikes(event.getPostId());
    }

    // 🔥 COMMENT ANALYTICS
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentCreated(CommentCreatedEvent event) {

        System.out.println("🔥 ANALYTICS COMMENT EVENT RECEIVED");

        analyticsService.incrementComments(event.getPostId());
    }
}