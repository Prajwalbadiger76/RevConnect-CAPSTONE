package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Post;
import com.example.demo.entity.PostAnalytics;
import com.example.demo.repo.PostAnalyticsRepository;
import com.example.demo.repo.PostRepository;

@Service
public class AnalyticsService {

    @Autowired
    private PostAnalyticsRepository analyticsRepository;

    @Autowired
    private PostRepository postRepository;

    public void createPostAnalytics(Long postId) {

        if (analyticsRepository.findByPost_Id(postId).isPresent())
            return;

        Post post = postRepository.findById(postId).orElseThrow();

        PostAnalytics analytics = new PostAnalytics();
        analytics.setPost(post);

        analyticsRepository.save(analytics);
    }

    private PostAnalytics getOrCreate(Long postId) {

        return analyticsRepository
                .findByPost_Id(postId)
                .orElseGet(() -> {
                    Post post = postRepository.findById(postId).orElseThrow();

                    PostAnalytics analytics = new PostAnalytics();
                    analytics.setPost(post);

                    return analyticsRepository.save(analytics);
                });
    }

    public void incrementLikes(Long postId) {

        PostAnalytics analytics = getOrCreate(postId);
        analytics.setTotalLikes(analytics.getTotalLikes() + 1);
        analyticsRepository.save(analytics);
    }

    public void incrementComments(Long postId) {

        PostAnalytics analytics = getOrCreate(postId);
        analytics.setTotalComments(analytics.getTotalComments() + 1);
        analyticsRepository.save(analytics);
    }

    public void incrementReach(Long postId) {

        PostAnalytics analytics = getOrCreate(postId);
        analytics.setReachCount(analytics.getReachCount() + 1);
        analyticsRepository.save(analytics);
    }
}