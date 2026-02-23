package com.example.demo.service;

import com.example.demo.dto.PostRequestDTO;
import com.example.demo.dto.PostDto;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repo.PostRepository;
import com.example.demo.repo.HashtagRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private HashtagRepository hashtagRepository;

    @InjectMocks
    private PostServiceImpl postService;

    // TEST CREATE POST
 
    @Test
    void testCreatePost_Success() {

        // Arrange
        PostRequestDTO dto = new PostRequestDTO();
        dto.setContent("Launching new product");
        dto.setPromotional(true);

        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Post savedPost = new Post();
        savedPost.setId(100L);
        savedPost.setContent(dto.getContent());
        savedPost.setUser(user);

        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        // Act
        PostDto result = postService.createPost(dto, userId);

        // Assert
        assertNotNull(result);
        assertEquals("Launching new product", result.getContent());

        verify(postRepository, times(1)).save(any(Post.class));
    }

    // TEST PIN POST

    @Test
    void testPinPost_Success() {

        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Post post = new Post();
        post.setId(10L);
        post.setUser(user);

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostDto result = postService.pinPost(10L, userId);

        assertTrue(post.isPinned());
        verify(postRepository, times(1)).save(post);
    }

    // TEST REPOST
   
    @Test
    void testRepost_Success() {

        Long originalUserId = 1L;
        Long newUserId = 2L;

        User originalUser = new User();
        originalUser.setId(originalUserId);

        Post originalPost = new Post();
        originalPost.setId(5L);
        originalPost.setContent("Original content");
        originalPost.setUser(originalUser);

        when(postRepository.findById(5L)).thenReturn(Optional.of(originalPost));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostDto result = postService.repostPost(5L, newUserId);

        assertNotNull(result);
        assertEquals("Original content", result.getContent());

        verify(postRepository, times(1)).save(any(Post.class));
    }
}