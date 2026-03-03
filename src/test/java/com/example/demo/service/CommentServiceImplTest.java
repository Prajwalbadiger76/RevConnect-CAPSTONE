package com.example.demo.service;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repo.CommentRepository;
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
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private EventPublisherService eventPublisherService;

    @InjectMocks
    private CommentServiceImpl commentService;

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
    void testAddComment_shouldSaveComment() {

        when(userRepository.findByUsername("prajwal"))
                .thenReturn(Optional.of(user));
        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        commentService.addComment("prajwal", 1L, "Nice post!");

        verify(commentRepository).save(any(Comment.class));
        verify(eventPublisherService)
                .publishCommentCreated(anyLong(), anyString(), anyString());
    }

    @Test
    void testDeleteComment_whenOwner_shouldDelete() {

        Comment comment = new Comment();
        comment.setUser(user);

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment));

        commentService.deleteComment("prajwal", 1L);

        verify(commentRepository).delete(comment);
    }

    @Test
    void testDeleteComment_whenNotOwner_shouldThrowException() {

        User otherUser = new User();
        otherUser.setUsername("other");

        Comment comment = new Comment();
        comment.setUser(otherUser);

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment));

        assertThrows(RuntimeException.class,
                () -> commentService.deleteComment("prajwal", 1L));
    }

    // ================= NEW POSITIVE TEST =================

    @Test
    void testAddComment_shouldSetCreatedAt() {

        when(userRepository.findByUsername("prajwal"))
                .thenReturn(Optional.of(user));
        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        commentService.addComment("prajwal", 1L, "Nice!");

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());

        Comment savedComment = captor.getValue();

        assertNotNull(savedComment.getCreatedAt());
        assertEquals("Nice!", savedComment.getContent());
    }

    // ================= NEW NEGATIVE TESTS =================

    @Test
    void testAddComment_whenUserNotFound_shouldThrowException() {

        when(userRepository.findByUsername("prajwal"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> commentService.addComment("prajwal", 1L, "Hello"));
    }

    @Test
    void testAddComment_whenPostNotFound_shouldThrowException() {

        when(userRepository.findByUsername("prajwal"))
                .thenReturn(Optional.of(user));
        when(postRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> commentService.addComment("prajwal", 1L, "Hello"));
    }

    @Test
    void testDeleteComment_whenCommentNotFound_shouldThrowException() {

        when(commentRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> commentService.deleteComment("prajwal", 1L));
    }
}