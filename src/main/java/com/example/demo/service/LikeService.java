package com.example.demo.service;

import com.example.demo.entity.Like;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repo.LikeRepository;
import com.example.demo.repo.PostRepository;
import com.example.demo.repo.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

   
    public LikeService(LikeRepository likeRepository,
                       PostRepository postRepository,
                       UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public void likePost(Long userId, Long postId) {

        if (likeRepository.findByUserIdAndPostId(userId, postId).isPresent()) {
            return; 
        }

        User user = userRepository.findById(userId).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);

        likeRepository.save(like);
    }

    public void unlikePost(Long userId, Long postId) {
        likeRepository.findByUserIdAndPostId(userId, postId)
                .ifPresent(likeRepository::delete);
    }

    public long getLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }
}