package com.example.demo.service;

import com.example.demo.entity.Follow;
import com.example.demo.entity.User;
import com.example.demo.repo.FollowRepository;
import com.example.demo.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public FollowServiceImpl(FollowRepository followRepository,
                             UserRepository userRepository,
                             NotificationService notificationService) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void follow(String currentUsername, String targetUsername) {

        if (currentUsername.equals(targetUsername)) return;

        User follower = userRepository.findByUsername(currentUsername).orElseThrow();
        User following = userRepository.findByUsername(targetUsername).orElseThrow();

        if (followRepository.findByFollowerAndFollowing(follower, following).isPresent()) {
            return;
        }

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);

        followRepository.save(follow);

        notificationService.createNotification(
                targetUsername,
                currentUsername,
                "FOLLOW",
                null
        );
    }

    @Override
    public void unfollow(String currentUsername, String targetUsername) {

        User follower = userRepository.findByUsername(currentUsername).orElseThrow();
        User following = userRepository.findByUsername(targetUsername).orElseThrow();

        followRepository.findByFollowerAndFollowing(follower, following)
                .ifPresent(followRepository::delete);
    }

    @Override
    public boolean isFollowing(String currentUsername, String targetUsername) {

        User follower = userRepository.findByUsername(currentUsername).orElseThrow();
        User following = userRepository.findByUsername(targetUsername).orElseThrow();

        return followRepository
                .findByFollowerAndFollowing(follower, following)
                .isPresent();
    }

    @Override
    public List<User> getFollowers(String username){

        User user = userRepository.findByUsername(username).orElseThrow();

        List<Follow> follows = followRepository.findByFollowing(user);

        List<User> followers = new ArrayList<>();

        for(Follow f : follows){
            followers.add(f.getFollower());
        }

        return followers;
    }

    @Override
    public List<User> getFollowing(String username){

        User user = userRepository.findByUsername(username).orElseThrow();

        List<Follow> follows = followRepository.findByFollower(user);

        List<User> following = new ArrayList<>();

        for(Follow f : follows){
            following.add(f.getFollowing());
        }

        return following;
    }
    
    @Override
    public long getFollowersCount(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return followRepository.countByFollowing_Id(user.getId());
    }
}