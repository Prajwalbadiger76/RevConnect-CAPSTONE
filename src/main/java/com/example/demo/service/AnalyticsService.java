package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.PostAnalytics;
import com.example.demo.repo.PostAnalyticsRepository;
import com.example.demo.repo.PostRepository;

@Service
public class AnalyticsService {

    @Autowired
    private PostAnalyticsRepository analyticsRepository;

    @Autowired
    private PostRepository postRepository;

//    public void createPostAnalytics(Long postId) {
//
//        PostAnalytics analytics = new PostAnalytics();
//        analytics.setPost(
//                postRepository.findById(postId).orElseThrow()
//        );
//
//        analyticsRepository.save(analytics);
//    }

    public void incrementLikes(Long postId) {
        PostAnalytics analytics = analyticsRepository
                .findByPostId(postId)
                .orElseThrow();

        analytics.setTotalLikes(analytics.getTotalLikes() + 1);
        analyticsRepository.save(analytics);
    }

    public void incrementComments(Long postId) {
        PostAnalytics analytics = analyticsRepository
                .findByPostId(postId)
                .orElseThrow();

        analytics.setTotalComments(analytics.getTotalComments() + 1);
        analyticsRepository.save(analytics);
    }

    public void incrementReach(Long postId) {
        PostAnalytics analytics = analyticsRepository
                .findByPostId(postId)
                .orElseThrow();

        analytics.setReachCount(analytics.getReachCount() + 1);
        analyticsRepository.save(analytics);
    }
}