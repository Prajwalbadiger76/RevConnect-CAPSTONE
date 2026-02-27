package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repo.UserRepository;
import com.example.demo.service.PostService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
    public String getFeed(Model model, Authentication authentication) {

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow();

        model.addAttribute("posts",
                postService.getFeedPosts(username));

        model.addAttribute("currentUsername", username);

        model.addAttribute("currentUserRole",
                user.getRole().name());  // if enum
        model.addAttribute("posts", postService.getFeedPosts(username));
        model.addAttribute("trendingHashtags", postService.getTrendingHashtags());  
        model.addAttribute("currentUsername", username);
        model.addAttribute("showCreateForm", true);

        return "feed";
    }
}