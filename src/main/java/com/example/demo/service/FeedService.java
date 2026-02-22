package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Post;

public interface FeedService {

    List<Post> getPersonalizedFeed(Long userId);

    List<String> getTrendingHashtags();

    List<Post> filterByUserRole(String role);
}