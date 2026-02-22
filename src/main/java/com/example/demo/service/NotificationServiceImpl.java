package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Notification;
import com.example.demo.entity.NotificationType;
import com.example.demo.entity.User;
import com.example.demo.repo.NotificationRepository;
import com.example.demo.repo.UserRepository;

@Service
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository repository;
	private final UserRepository userRepository;

	public NotificationServiceImpl(NotificationRepository repository, UserRepository userRepository) {
		this.repository = repository;
		this.userRepository = userRepository;
	}

	@Override
	public void createNotification(Long recipientId, Long senderId, NotificationType type, Long referenceId) {

		User recipient = userRepository.findById(recipientId).orElseThrow();
		User sender = userRepository.findById(senderId).orElseThrow();

		Notification notification = new Notification();
		notification.setRecipient(recipient);
		notification.setSender(sender);
		notification.setType(type);
		notification.setReferenceId(referenceId);

		repository.save(notification);
	}

	@Override
	public List<Notification> getUserNotifications(Long userId) {
		return repository.findByRecipientIdOrderByCreatedAtDesc(userId);
	}

	@Override
	public long getUnreadCount(Long userId) {
		return repository.countByRecipientIdAndIsReadFalse(userId);
	}

	@Override
	public void markAsRead(Long id) {
		Notification n = repository.findById(id).orElseThrow();
		n.setRead(true);
		repository.save(n);
	}
}