package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.User;

public interface FollowService {

    void follow(String currentUsername, String targetUsername);

    void unfollow(String currentUsername, String targetUsername);

    boolean isFollowing(String currentUsername, String targetUserName);
    
    List<User> getFollowers(String username);
    
    List<User> getFollowing(String username);
    
    long getFollowersCount(String username);
}