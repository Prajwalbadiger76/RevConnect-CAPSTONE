package com.example.demo.controller;

import com.example.demo.service.PostService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FeedController {

    private final PostService postService;

    public FeedController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/feed")
    public String getFeed(Model model, Authentication authentication) {

        String username = authentication.getName();

        model.addAttribute("posts", postService.getFeedPosts(username));
        model.addAttribute("trendingHashtags", postService.getTrendingHashtags());  
        model.addAttribute("currentUsername", username);
        model.addAttribute("showCreateForm", true);

        return "feed";
    }
}