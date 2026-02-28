package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

//import com.example.demo.dto.PostRequestDTO;
import com.example.demo.entity.*;
import com.example.demo.entity.Post;

import com.example.demo.repo.PostRepository;
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

    
    
//    @Test
//    void testSavePost_Success() {
//        // 1. Setup Data
//        PostRequestDTO dto = new PostRequestDTO();
//        dto.setContent("Testing without logging");
//        Long userId = 1L;
//
//        Post mockSavedPost = new Post();
//        mockSavedPost.setId(99L);
//        mockSavedPost.setContent(dto.getContent());
//
//        // 2. Define Mock Behavior
//        when(postRepository.save(any(Post.class))).thenReturn(mockSavedPost);
//
//        // 3. Execute
//        Post result = postService.savePost(dto, userId);
//
//        // 4. Verify results (JUnit)
//        assertNotNull(result);
//        assertEquals(99L, result.getId());
//        assertEquals("Testing without logging", result.getContent());
//        
//        // Ensure the repository was actually called
//        verify(postRepository, times(1)).save(any(Post.class));
//    }
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