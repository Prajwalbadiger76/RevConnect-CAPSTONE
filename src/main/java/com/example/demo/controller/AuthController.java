package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

// Log4j imports
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // ================= REGISTER =================

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterRequest request,
                           BindingResult result,
                           Model model) {

        logger.info("Register request received for username: {}", request.username());

        if (result.hasErrors()) {
            logger.warn("Validation failed during registration for username: {}", request.username());
            return "register";
        }

        try {

            userService.register(request);

            logger.info("User successfully registered: {}", request.username());

            return "redirect:/login";

        } catch (com.example.demo.exception.CustomException ex) {

            logger.warn("Registration failed: {}", ex.getMessage());

            model.addAttribute("error", ex.getMessage());

            return "register";   // stay on same page
        }
    }


    // ================= LOGIN =================

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginRequest request,
                        HttpServletResponse response,
                        org.springframework.ui.Model model) {

        logger.info("Login attempt for username: {}", request.username());

        try {

            String token = userService.login(request);

            Cookie jwtCookie = new Cookie("JWT", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(60 * 60);

            response.addCookie(jwtCookie);

            logger.info("Login successful for username: {}", request.username());

            return "redirect:/feed";

        } catch (com.example.demo.exception.CustomException ex) {

            logger.error("Login failed for username: {}", request.username());

            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }


    // ================= LOGOUT =================

    @GetMapping("/logout")
    public String logout(HttpServletResponse response,
                         jakarta.servlet.http.HttpServletRequest request) {

        logger.info("Logout request received");

        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        org.springframework.security.core.context.SecurityContextHolder.clearContext();

        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        logger.info("User successfully logged out");

        return "redirect:/login";
    }
}