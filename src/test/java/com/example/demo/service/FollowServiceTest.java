package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repo.FollowRepository;
import com.example.demo.repo.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class FollowServiceTest {

    @Autowired
    private FollowService followService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    @MockBean
    private NotificationService notificationService;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {

        user1 = new User();
        user1.setUsername("follow_user1");
        user1.setEmail("f1@test.com");
        user1.setPassword("123");
        user1.setRole(Role.PERSONAL);
        userRepository.save(user1);

        user2 = new User();
        user2.setUsername("follow_user2");
        user2.setEmail("f2@test.com");
        user2.setPassword("123");
        user2.setRole(Role.PERSONAL);
        userRepository.save(user2);
    }

    // ✅ POSITIVE — FOLLOW
    @Test
    void follow_shouldCreateRelationship() {

        followService.follow("follow_user1", "follow_user2");

        assertTrue(
                followService.isFollowing("follow_user1", "follow_user2"));
    }

    // ❌ NEGATIVE — SELF FOLLOW
    @Test
    void follow_shouldNotAllowSelfFollow() {

        followService.follow("follow_user1", "follow_user1");

        assertFalse(
                followService.isFollowing("follow_user1", "follow_user1"));
    }

    // ❌ NEGATIVE — DUPLICATE FOLLOW
    @Test
    void follow_shouldNotCreateDuplicate() {

        followService.follow("follow_user1", "follow_user2");
        followService.follow("follow_user1", "follow_user2");

        long count =
                followRepository.findByFollowerAndFollowing(user1, user2)
                        .stream().count();

        assertEquals(1, count);
    }

    // ✅ POSITIVE — UNFOLLOW
    @Test
    void unfollow_shouldRemoveRelationship() {

        followService.follow("follow_user1", "follow_user2");

        followService.unfollow("follow_user1", "follow_user2");

        assertFalse(
                followService.isFollowing("follow_user1", "follow_user2"));
    }
}