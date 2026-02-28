package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.NotificationPreference;
import com.example.demo.entity.User;

public interface NotificationPreferenceRepository
extends JpaRepository<NotificationPreference, Long> {

Optional<NotificationPreference> findByUser(User user);
}