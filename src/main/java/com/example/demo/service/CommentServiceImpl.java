package com.example.demo.service;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repo.CommentRepository;
import com.example.demo.repo.PostRepository;
import com.example.demo.repo.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final EventPublisherService eventPublisherService;

    public CommentServiceImpl(CommentRepository commentRepository,
                              UserRepository userRepository,
                              PostRepository postRepository,
                              EventPublisherService eventPublisherService) {

        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.eventPublisherService = eventPublisherService;
    }

    // ================= ADD COMMENT =================

    @Override
    @Transactional
    public void addComment(String username, Long postId, String content) {

        User user = userRepository.findByUsername(username).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(user);
        comment.setPost(post);

        commentRepository.save(comment);
        

        eventPublisherService.publishCommentCreated(
                post.getId(),
                username,
                post.getUser().getUsername()
        );
    }

    // ================= DELETE COMMENT =================

    @Override
    public void deleteComment(String username, Long commentId) {

        Comment comment = commentRepository.findById(commentId).orElseThrow();

        if (!comment.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You cannot delete this comment");
        }

        commentRepository.delete(comment);
    }
}