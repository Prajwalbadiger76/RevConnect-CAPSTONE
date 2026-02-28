package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingsPageController {

    @GetMapping("/settings/notifications")
    public String notificationSettingsPage() {
        return "notification-settings";
    }
}