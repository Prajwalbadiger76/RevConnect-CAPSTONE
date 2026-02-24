package com.example.demo.listener;

import com.example.demo.events.LikeCreatedEvent;
import com.example.demo.service.NotificationService;
import com.example.demo.service.AnalyticsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
public class LikeEventHandler {

    private final NotificationService notificationService;
    private final AnalyticsService analyticsService;

    public LikeEventHandler(NotificationService notificationService,
                            AnalyticsService analyticsService) {
        this.notificationService = notificationService;
        this.analyticsService = analyticsService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLikeCreated(LikeCreatedEvent event) {

        System.out.println("LIKE EVENT RECEIVED");

        notificationService.createNotification(
                event.getRecipientUsername(),
                event.getSenderUsername(),
                "LIKE",
                event.getPostId()
        );

        analyticsService.incrementLikes(event.getPostId());
    }
}