package com.example.demo.service;

import com.example.demo.dto.NotificationDto;
import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.repo.NotificationRepository;
import com.example.demo.repo.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User recipient;
    private User sender;

    @BeforeEach
    void setup() {
        recipient = new User();
        recipient.setUsername("testuser");

        sender = new User();
        sender.setUsername("usertest");
    }

    // POSITIVE TEST CASE
    @Test
    void testCreateNotification_Success() {

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(recipient));

        when(userRepository.findByUsername("usertest"))
                .thenReturn(Optional.of(sender));

        notificationService.createNotification(
                "testuser",
                "usertest",
                "LIKE",
                10L
        );

        verify(notificationRepository, times(1))
                .save(any(Notification.class));
    }

    // NEGATIVE TEST CASE
    @Test
    void testCreateNotification_UserNotFound() {

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            notificationService.createNotification(
                    "testuser",
                    "usertest",
                    "LIKE",
                    10L
            );
        });

        verify(notificationRepository, never())
                .save(any(Notification.class));
    }

    // GET NOTIFICATIONS
    @Test
    void testGetUserNotifications() {

        Notification notification =
                new Notification(recipient, sender, "LIKE", 10L);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(recipient));

        when(notificationRepository.findByRecipientOrderByCreatedAtDesc(recipient))
                .thenReturn(List.of(notification));

        List<NotificationDto> result =
                notificationService.getUserNotifications("testuser");

        assertEquals(1, result.size());
        assertTrue(result.get(0).getMessage().contains("liked your post"));
    }

    // UNREAD COUNT
    @Test
    void testGetUnreadCount() {

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(recipient));

        when(notificationRepository.countByRecipientAndIsReadFalse(recipient))
                .thenReturn(3L);

        long count = notificationService.getUnreadCount("testuser");

        assertEquals(3L, count);
    }

    // MARK AS READ
    @Test
    void testMarkAsRead() {

        Notification notification =
                new Notification(recipient, sender, "LIKE", 10L);

        when(notificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        notificationService.markAsRead(1L);

        assertTrue(notification.isRead());
    }

    // NEGATIVE
    @Test
    void testMarkAsRead_NotFound() {

        when(notificationRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            notificationService.markAsRead(1L);
        });
    }
}