package com.example.demo.service;

import com.example.demo.entity.NotificationPreference;

public interface NotificationPreferenceService {

    NotificationPreference getPreferences(String username);

    void updatePreferences(String username,
                           NotificationPreference updated);
}