package com.example.demo.controller;


import com.example.demo.entity.Post;

import com.example.demo.entity.Share;
import com.example.demo.entity.User;
import com.example.demo.repo.PostRepository;
import com.example.demo.repo.ShareRepository;
import com.example.demo.repo.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shares")
public class ShareController {

    private final ShareRepository shareRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public ShareController(ShareRepository shareRepository,
                           UserRepository userRepository,
                           PostRepository postRepository) {
        this.shareRepository = shareRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @PostMapping
    public String sharePost(@RequestParam Long userId,
                            @RequestParam Long postId) {

        User user = userRepository.findById(userId).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        Share share = new Share();
        share.setUser(user);
        share.setPost(post);

        shareRepository.save(share);

        return "Post shared successfully";
    }
}
