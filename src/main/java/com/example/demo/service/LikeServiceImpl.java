package com.example.demo.service;

import com.example.demo.entity.Like;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repo.LikeRepository;
import com.example.demo.repo.PostRepository;
import com.example.demo.repo.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final EventPublisherService eventPublisherService;

    public LikeServiceImpl(LikeRepository likeRepository,
                           UserRepository userRepository,
                           PostRepository postRepository,
                           EventPublisherService eventPublisherService) {

        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.eventPublisherService = eventPublisherService;
    }

    @Override
    @Transactional
    public void toggleLike(String username, Long postId) {

        User user = userRepository.findByUsername(username).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        likeRepository.findByUserAndPost(user, post)
                .ifPresentOrElse(
                        existing -> likeRepository.delete(existing),
                        () -> {
                            Like like = new Like();
                            like.setUser(user);
                            like.setPost(post);
                            likeRepository.save(like);

                            // 🔔 PUBLISH EVENT ONLY WHEN NEW LIKE CREATED
                            eventPublisherService.publishLikeCreated(
                                    post.getId(),
                                    user.getUsername(),
                                    post.getUser().getUsername()
                            );
                        }
                );
    }

    @Override
    public long getLikeCount(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow();
        return likeRepository.countByPost(post);
    }

    @Override
    public boolean isLikedByUser(String username, Long postId) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();
        return likeRepository.findByUserAndPost(user, post).isPresent();
    }
}