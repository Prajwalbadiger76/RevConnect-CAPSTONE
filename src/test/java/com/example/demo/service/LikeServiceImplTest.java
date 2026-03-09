package com.example.demo.service;

import com.example.demo.entity.Like;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repo.LikeRepository;
import com.example.demo.repo.PostRepository;
import com.example.demo.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class LikeServiceImplTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private EventPublisherService eventPublisherService;

    @InjectMocks
    private LikeServiceImpl likeService;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("prajwal");

        post = new Post();
        post.setUser(user);

        ReflectionTestUtils.setField(post, "id", 1L);
    }

    // ================= EXISTING TESTS =================

    @Test
    void testToggleLike_whenLikeDoesNotExist_shouldCreateLike() {

        when(userRepository.findByUsername("prajwal"))
                .thenReturn(Optional.of(user));
        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(user, post))
                .thenReturn(Optional.empty());

        likeService.toggleLike("prajwal", 1L);

        verify(likeRepository).save(any(Like.class));
        verify(eventPublisherService)
                .publishLikeCreated(anyLong(), anyString(), anyString());
    }

    @Test
    void testToggleLike_whenLikeExists_shouldDeleteLike() {

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);

        when(userRepository.findByUsername("prajwal"))
                .thenReturn(Optional.of(user));
        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(user, post))
                .thenReturn(Optional.of(like));

        likeService.toggleLike("prajwal", 1L);

        verify(likeRepository).delete(like);
        verify(eventPublisherService, never())
                .publishLikeCreated(anyLong(), anyString(), anyString());
    }

    // ================= NEW POSITIVE TESTS =================

    @Test
    void testGetLikeCount_shouldReturnCorrectCount() {

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));
        when(likeRepository.countByPost(post))
                .thenReturn(5L);

        long count = likeService.getLikeCount(1L);

        assertEquals(5L, count);
    }

    @Test
    void testIsLikedByUser_whenLikeExists_shouldReturnTrue() {

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);

        when(userRepository.findByUsername("prajwal"))
                .thenReturn(Optional.of(user));
        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(user, post))
                .thenReturn(Optional.of(like));

        boolean result = likeService.isLikedByUser("prajwal", 1L);

        assertTrue(result);
    }

    @Test
    void testIsLikedByUser_whenLikeNotExists_shouldReturnFalse() {

        when(userRepository.findByUsername("prajwal"))
                .thenReturn(Optional.of(user));
        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));
        when(likeRepository.findByUserAndPost(user, post))
                .thenReturn(Optional.empty());

        boolean result = likeService.isLikedByUser("prajwal", 1L);

        assertFalse(result);
    }

    // ================= NEW NEGATIVE TESTS =================

    @Test
    void testToggleLike_whenUserNotFound_shouldThrowException() {

        when(userRepository.findByUsername("prajwal"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> likeService.toggleLike("prajwal", 1L));
    }

    @Test
    void testToggleLike_whenPostNotFound_shouldThrowException() {

        when(userRepository.findByUsername("prajwal"))
                .thenReturn(Optional.of(user));
        when(postRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> likeService.toggleLike("prajwal", 1L));
    }

    @Test
    void testGetLikeCount_whenPostNotFound_shouldThrowException() {

        when(postRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> likeService.getLikeCount(1L));
    }
}