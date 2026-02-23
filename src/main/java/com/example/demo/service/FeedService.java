package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Posts;

public interface FeedService {

    List<Posts> getPersonalizedFeed(Long userId);

    List<String> getTrendingHashtags();

    List<Posts> filterByUserRole(String role);
}

