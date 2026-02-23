package com.example.demo.controller;


import com.example.demo.service.LikeService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

  
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping
    public String likePost(@RequestParam Long userId,
                           @RequestParam Long postId) {

        likeService.likePost(userId, postId);
        return "Post liked successfully";
    }

    @DeleteMapping
    public String unlikePost(@RequestParam Long userId,
                             @RequestParam Long postId) {

        likeService.unlikePost(userId, postId);
        return "Post unliked successfully";
    }

    @GetMapping("/count/{postId}")
    public long getLikeCount(@PathVariable Long postId) {
        return likeService.getLikeCount(postId);
    }
}
