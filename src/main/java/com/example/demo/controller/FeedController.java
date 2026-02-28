package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repo.UserRepository;
import com.example.demo.service.PostService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FeedController {

    private final PostService postService;
    private final UserRepository userRepository;   

    public FeedController(PostService postService,
                          UserRepository userRepository) {
        this.postService = postService;
        this.userRepository = userRepository;   
    }

    @GetMapping("/feed")
    public String getFeed(@RequestParam(required = false) String role,
                          Model model,
                          Authentication authentication) {

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow();

        // ===== get filtered or normal feed =====
        model.addAttribute("posts",
                postService.getFeedPosts(username, role));

        // remember selected filter in UI
        model.addAttribute("selectedRole",
                role == null ? "ALL" : role);

        model.addAttribute("currentUsername", username);
        model.addAttribute("currentUserRole", user.getRole().name());

        model.addAttribute("trendingHashtags",
                postService.getTrendingHashtags());

        model.addAttribute("showCreateForm", true);

        return "feed";
    }
}