package com.example.demo.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.demo.events.CommentCreatedEvent;
import com.example.demo.events.FollowEvent;
import com.example.demo.events.LikeCreatedEvent;
import com.example.demo.events.PostCreatedEvent;
import com.example.demo.service.AnalyticsService;
import com.example.demo.service.NotificationService;

@Component
public class SocialEventListener {

    private final NotificationService notificationService;
    private final AnalyticsService analyticsService;

    public SocialEventListener(NotificationService notificationService,
                               AnalyticsService analyticsService) {
        this.notificationService = notificationService;
        this.analyticsService = analyticsService;
    }

//    @EventListener
//    public void handlePost(PostCreatedEvent event) {
//        analyticsService.createPostAnalytics(event.getPostId());
//        notificationService.notifyFollowersNewPost(event);
//    }
//
//    @EventListener
//    public void handleLike(LikeCreatedEvent event) {
//        analyticsService.incrementLikes(event.getPostId());
//        notificationService.notifyPostLike(event);
//    }
//
//    @EventListener
//    public void handleComment(CommentCreatedEvent event) {
//        analyticsService.incrementComments(event.getPostId());
//        notificationService.notifyPostComment(event);
//    }
//
//    @EventListener
//    public void handleFollow(FollowEvent event) {
//        notificationService.notifyFollow(event);
//    }
//
//    @EventListener
//    public void handleConnection(ConnectionEvent event) {
//        if ("PENDING".equals(event.getStatus())) {
//            notificationService.notifyConnectionRequest(event);
//        }
//        if ("ACCEPTED".equals(event.getStatus())) {
//            notificationService.notifyConnectionAccepted(event);
//        }
//    }
}