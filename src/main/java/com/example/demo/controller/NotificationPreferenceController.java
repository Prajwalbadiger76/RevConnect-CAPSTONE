package com.example.demo.controller;

import com.example.demo.entity.NotificationPreference;
import com.example.demo.service.NotificationPreferenceService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification-preferences")
public class NotificationPreferenceController {

    private final NotificationPreferenceService service;

    public NotificationPreferenceController(NotificationPreferenceService service) {
        this.service = service;
    }

    // Get preferences
    @GetMapping
    public NotificationPreference get(Authentication auth) {
        return service.getPreferences(auth.getName());
    }

    // Update preferences
    @PutMapping
    public void update(Authentication auth,
                       @RequestBody NotificationPreference pref) {
        service.updatePreferences(auth.getName(), pref);
    }
}