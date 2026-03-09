package com.example.demo.service;

import com.example.demo.dto.NotificationDto;
import com.example.demo.entity.Notification;
import com.example.demo.entity.NotificationPreference;
import com.example.demo.entity.User;
import com.example.demo.repo.NotificationPreferenceRepository;
import com.example.demo.repo.NotificationRepository;
import com.example.demo.repo.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger =
            LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationPreferenceRepository preferenceRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository,
                                   NotificationPreferenceRepository preferenceRepository) {

        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.preferenceRepository = preferenceRepository;
    }

    // =====================================================
    // CREATE NOTIFICATION (WITH PREFERENCE CHECK)
    // =====================================================

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createNotification(String recipientUsername,
                                   String senderUsername,
                                   String type,
                                   Long referenceId) {

        logger.info("Creating notification -> sender: {}, recipient: {}, type: {}",
                senderUsername, recipientUsername, type);

        if (recipientUsername.equals(senderUsername)) {
            logger.debug("Skipping self-notification for user {}", senderUsername);
            return;
        }

        User recipient = userRepository.findByUsername(recipientUsername)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        NotificationPreference pref =
                preferenceRepository.findByUser(recipient)
                        .orElse(null);

        if (pref != null) {

            if ("LIKE".equals(type) && !pref.isLikeEnabled()) {
                logger.info("LIKE notification disabled for user {}", recipientUsername);
                return;
            }

            if ("COMMENT".equals(type) && !pref.isCommentEnabled()) {
                logger.info("COMMENT notification disabled for user {}", recipientUsername);
                return;
            }

            if ("FOLLOW".equals(type) && !pref.isFollowEnabled()) {
                logger.info("FOLLOW notification disabled for user {}", recipientUsername);
                return;
            }
        }

        Notification notification =
                new Notification(recipient, sender, type, referenceId);

        notificationRepository.save(notification);

        logger.info("Notification saved successfully for recipient {}", recipientUsername);
    }

    // =====================================================
    // GET NOTIFICATIONS
    // =====================================================

    @Override
    public List<NotificationDto> getUserNotifications(String username) {

        logger.info("Fetching notifications for user {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<NotificationDto> notifications =
                notificationRepository
                        .findByRecipientOrderByCreatedAtDesc(user)
                        .stream()
                        .map(n -> new NotificationDto(
                                n.getId(),
                                buildMessage(n),
                                n.isRead(),
                                n.getReferenceId(),
                                n.getCreatedAt()
                        ))
                        .collect(Collectors.toList());

        logger.info("Total notifications fetched for {}: {}", username, notifications.size());

        return notifications;
    }

    // =====================================================
    // UNREAD COUNT
    // =====================================================

    @Override
    public long getUnreadCount(String username) {

        logger.debug("Fetching unread notification count for {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long count = notificationRepository.countByRecipientAndIsReadFalse(user);

        logger.debug("Unread notifications for {}: {}", username, count);

        return count;
    }

    // =====================================================
    // MARK AS READ
    // =====================================================

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {

        logger.info("Marking notification {} as read", notificationId);

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);

        logger.info("Notification {} marked as read", notificationId);
    }

    // =====================================================
    // MESSAGE BUILDER
    // =====================================================

    private String buildMessage(Notification n) {

        return switch (n.getType()) {

            case "LIKE" ->
                    n.getSender().getUsername() + " liked your post";

            case "COMMENT" ->
                    n.getSender().getUsername() + " commented on your post";

            case "FOLLOW" ->
                    n.getSender().getUsername() + " started following you";

            case "CONNECTION_REQUEST" ->
                    n.getSender().getUsername() + " sent you a connection request";

            case "CONNECTION_ACCEPTED" ->
                    n.getSender().getUsername() + " accepted your connection request";

            default ->
                    "New notification";
        };
    }
}