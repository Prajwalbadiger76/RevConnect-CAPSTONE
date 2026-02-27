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

    public LikeEventHandler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLikeCreated(LikeCreatedEvent event) {

        System.out.println("LIKE EVENT RECEIVED (NOTIFICATION)");

        notificationService.createNotification(
                event.getRecipientUsername(),
                event.getSenderUsername(),
                "LIKE",
                event.getPostId()
        );
    }
}