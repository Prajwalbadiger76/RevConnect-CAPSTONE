package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterRequest request) {

        userService.register(request);

        return "redirect:/login";
    }
    
    
    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest request,
                        HttpServletResponse response) {

        String token = userService.login(request);

        Cookie jwtCookie = new Cookie("JWT", token);
        jwtCookie.setHttpOnly(true);   // security
        jwtCookie.setPath("/");        // available everywhere
        jwtCookie.setMaxAge(60 * 60);  // 1 hour

        response.addCookie(jwtCookie);

        return "redirect:/api/profile/me";
    }
    }
    
