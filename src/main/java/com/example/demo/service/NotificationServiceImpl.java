package com.example.demo.service;

import com.example.demo.dto.NotificationDto;
import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.repo.NotificationRepository;
import com.example.demo.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createNotification(String recipientUsername,
                                   String senderUsername,
                                   String type,
                                   Long referenceId) {

        User recipient = userRepository.findByUsername(recipientUsername)
                .orElseThrow();

        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow();

        Notification notification =
                new Notification(recipient, sender, type, referenceId);

        notificationRepository.save(notification);

        System.out.println("NOTIFICATION SAVED INTO DATABASE");
    }

    @Override
    public List<NotificationDto> getUserNotifications(String username) {

        User user = userRepository.findByUsername(username).orElseThrow();

        return notificationRepository
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
    }

    @Override
    public long getUnreadCount(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return notificationRepository.countByRecipientAndIsReadFalse(user);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification =
                notificationRepository.findById(notificationId).orElseThrow();
        notification.setRead(true);
    }

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