package com.example.demo.service;

import com.example.demo.entity.NotificationType;
import java.util.List;

public interface NotificationService {

    void createNotification(Long recipientId,
                            Long senderId,
                            NotificationType type,
                            Long referenceId);

    List<?> getUserNotifications(Long userId);

    long getUnreadCount(Long userId);

    void markAsRead(Long notificationId);
}