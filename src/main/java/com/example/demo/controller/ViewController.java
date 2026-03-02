package com.example.demo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.dto.RegisterRequest;

@Controller
public class ViewController {

    // ===== Login Page =====
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // ===== Register Page =====
    @GetMapping("/register")
    public String showRegisterPage(Model model) {

        model.addAttribute("registerRequest",
                new RegisterRequest("", "", "", null));

        return "register";
    }

    // ===== Optional: Home Page =====
    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/login";
    }
}

