package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.service.NotificationService;
import com.example.demo.service.UserService;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;
    private final UserService userService;

    public NotificationController(NotificationService service,
                                  UserService userService) {
        this.service = service;
        this.userService = userService;
    }

//    @GetMapping
//    public String showNotifications(Model model) {
//
//        Long userId = userService.getCurrentUserId();
//
//        model.addAttribute("notifications",
//                service.getUserNotifications(userId));
//
//        model.addAttribute("unreadCount",
//                service.getUnreadCount(userId));
//
//        return "notifications";
//    }

    @GetMapping("/read/{id}")
    public String markRead(@PathVariable Long id) {

        service.markAsRead(id);

        return "redirect:/notifications";
    }
}