package com.example.demo.service;

import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repo.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HashtagRepository hashtagRepository;

    @Mock
    private PostHashtagRepository postHashtagRepository;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void testCreatePost_Success() {

        // 1️⃣ Mock user
        User mockUser = new User();
        mockUser.setUsername("testuser");

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(mockUser));

        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // 2️⃣ Call service
        postService.createPost("testuser", "Testing post content", "trend", "");

        // 3️⃣ Verify save was called
        verify(postRepository, times(1)).save(any(Post.class));
    }
}