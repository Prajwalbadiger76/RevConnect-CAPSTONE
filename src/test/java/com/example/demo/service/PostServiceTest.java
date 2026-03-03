package com.example.demo.service;

import com.example.demo.dto.PostDto;
import com.example.demo.entity.*;
import com.example.demo.exception.InvalidScheduleException;
import com.example.demo.mapper.PostMapper;
import com.example.demo.repo.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PostServiceTest {

    @Mock private PostRepository postRepository;
    @Mock private HashtagRepository hashtagRepository;
    @Mock private PostHashtagRepository postHashtagRepository;
    @Mock private UserRepository userRepository;
    @Mock private FollowRepository followRepository;
    @Mock private LikeRepository likeRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private NotificationService notificationService;
    @Mock private AnalyticsService analyticsService;
    @Mock private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setUsername("Esha");
        user.setRole(Role.BUSINESS);

        post = new Post();
        post.setUser(user);
        post.setContent("Test Post");
        post.setCreatedAt(LocalDateTime.now());
    }

    // ==================================================
    // 1️⃣ CREATE POST SUCCESS
    // ==================================================
    @Test
    void createPost_Success() {

        when(userRepository.findByUsername("Esha"))
                .thenReturn(Optional.of(user));

        when(postRepository.save(any(Post.class)))
                .thenReturn(post);

        postService.createPost(
                "Esha",
                "Test Post",
                "#java",
                null,
                false,
                null,
                null,
                null
        );

        verify(postRepository, atLeastOnce()).save(any(Post.class));
        verify(analyticsService, atLeastOnce()).createPostAnalytics(any(Post.class));
    }

    // ==================================================
    // 2️⃣ CREATE POST USER NOT FOUND
    // ==================================================
    @Test
    void createPost_UserNotFound() {

        when(userRepository.findByUsername("Esha"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                postService.createPost(
                        "Esha",
                        "Test",
                        null,
                        null,
                        false,
                        null,
                        null,
                        null
                )
        );
    }

    // ==================================================
    // 3️⃣ CREATE POST INVALID SCHEDULE
    // ==================================================
    @Test
    void createPost_InvalidSchedule() {

        when(userRepository.findByUsername("Esha"))
                .thenReturn(Optional.of(user));

        String pastTime = "2020-01-01T10:00";

        assertThrows(InvalidScheduleException.class, () ->
                postService.createPost(
                        "Esha",
                        "Test",
                        null,
                        pastTime,
                        false,
                        null,
                        null,
                        null
                )
        );
    }

    // ==================================================
    // 4️⃣ GET POST BY ID SUCCESS
    // ==================================================
    @Test
    void getPostById_Success() {

        when(userRepository.findByUsername("Esha"))
                .thenReturn(Optional.of(user));

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        when(likeRepository.countByPost(post)).thenReturn(0L);
        when(likeRepository.findByUserAndPost(user, post))
                .thenReturn(Optional.empty());

        when(commentRepository.findByPostOrderByCreatedAtAsc(post))
                .thenReturn(Collections.emptyList());

        when(analyticsService.getAnalyticsByPostId(anyLong()))
                .thenReturn(null);

        PostDto dto = postService.getPostById(1L, "Esha");

        assertNotNull(dto);
        verify(postRepository).findById(1L);
    }

    // ==================================================
    // 5️⃣ GET POST BY ID NOT FOUND
    // ==================================================
    @Test
    void getPostById_NotFound() {

        when(userRepository.findByUsername("Esha"))
                .thenReturn(Optional.of(user));

        when(postRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                postService.getPostById(1L, "Esha")
        );
    }

    // ==================================================
    // 6️⃣ UPDATE POST SUCCESS
    // ==================================================
    @Test
    void updatePost_Success() {

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        postService.updatePost(
                1L,
                "Updated Content",
                "#spring",
                "Esha"
        );

        verify(postRepository).save(post);
        verify(postHashtagRepository).deleteByPost(post);
    }

    // ==================================================
    // 7️⃣ DELETE POST SUCCESS
    // ==================================================
    @Test
    void deletePost_Success() {

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        postService.deletePost(1L, "Esha");

        verify(postRepository).delete(post);
    }
    
    @Test
    void updatePost_WrongUser_ShouldThrow() {

        User otherUser = new User();
        otherUser.setUsername("OtherUser");

        post.setUser(otherUser);

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                postService.updatePost(
                        1L,
                        "Updated",
                        "#java",
                        "Esha"   // Esha is NOT owner
                )
        );

        assertEquals("You cannot edit this post", ex.getMessage());
    }
    
    @Test
    void deletePost_WrongUser_ShouldThrow() {

        User otherUser = new User();
        otherUser.setUsername("OtherUser");

        post.setUser(otherUser);

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                postService.deletePost(1L, "Esha")
        );

        assertEquals("You cannot delete this post", ex.getMessage());
    }
    
    @Test
    void pinPost_MaxLimitExceeded_ShouldThrow() {

        when(userRepository.findByUsername("Esha"))
                .thenReturn(Optional.of(user));

        when(postRepository.countByUserAndPinnedTrue(user))
                .thenReturn(3L); // Already 3 pinned

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                postService.pinPost(1L, "Esha")
        );

        assertEquals("Maximum 3 pinned posts allowed", ex.getMessage());
    }
    
    
}