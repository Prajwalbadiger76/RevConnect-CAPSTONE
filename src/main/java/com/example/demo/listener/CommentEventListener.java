package com.example.demo.listener;

import com.example.demo.events.CommentCreatedEvent;
import com.example.demo.service.NotificationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
public class CommentEventListener {

    private final NotificationService notificationService;

    public CommentEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentCreated(CommentCreatedEvent event) {

        System.out.println("COMMENT EVENT RECEIVED");

        notificationService.createNotification(
                event.getRecipientUsername(),
                event.getSenderUsername(),
                "COMMENT",
                event.getPostId()
        );
    }
}