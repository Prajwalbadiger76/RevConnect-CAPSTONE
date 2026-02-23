package com.example.demo.service;

import com.example.demo.entity.Post;
import com.example.demo.entity.Share;
import com.example.demo.entity.User;
import com.example.demo.repo.PostRepository;
import com.example.demo.repo.ShareRepository;
import com.example.demo.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ShareService {

    private final ShareRepository shareRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public ShareService(ShareRepository shareRepository,
                        PostRepository postRepository,
                        UserRepository userRepository) {
        this.shareRepository = shareRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Share sharePost(Long userId, Long postId) {

        User user = userRepository.findById(userId).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        Share share = new Share();
        share.setUser(user);
        share.setPost(post);
        share.setSharedAt(LocalDateTime.now());

        return shareRepository.save(share);
    }

    public long getShareCount(Long postId) {
        return shareRepository.countByPostId(postId);
    }
}