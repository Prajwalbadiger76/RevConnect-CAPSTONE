package com.example.demo.service;

import com.example.demo.dto.PostDto;
import com.example.demo.dto.PostRequestDTO;
import com.example.demo.entity.*;
import com.example.demo.mapper.PostMapper;
import com.example.demo.repo.*;
import com.example.demo.exception.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private HashtagRepository hashtagRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public PostDto createPost(PostRequestDTO dto, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = new Post();
        post.setContent(dto.getContent());
        post.setPromotional(dto.isPromotional());
        post.setCtaLink(dto.getCtaLink());
        post.setScheduledTime(dto.getScheduledTime());
        post.setUser(user);

        return PostMapper.toDto(postRepository.save(post));
    }

    @Override
    public PostDto updatePost(Long postId, PostRequestDTO dto, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUser().getId().equals(userId))
            throw new UnauthorizedActionException("Unauthorized");

        post.setContent(dto.getContent());
        post.setScheduledTime(dto.getScheduledTime());

        return PostMapper.toDto(postRepository.save(post));
    }

    @Override
    public void deletePost(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUser().getId().equals(userId))
            throw new UnauthorizedActionException("Unauthorized");

        postRepository.delete(post);
    }

    @Override
    public List<PostDto> getFeed() {

        List<Post> posts =
                postRepository.findByScheduledTimeIsNullOrScheduledTimeBefore(LocalDateTime.now());

        return posts.stream()
                .map(PostMapper::toDto)
                .toList();
    }

    @Override
    public PostDto pinPost(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUser().getId().equals(userId))
            throw new UnauthorizedActionException("Unauthorized");

        post.setPinned(true);

        return PostMapper.toDto(postRepository.save(post));
    }

    @Override
    public PostDto repostPost(Long postId, Long userId) {

        Post original = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Original post not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (original.getUser().getId().equals(userId))
            throw new CustomException("Cannot repost your own post");

        Post repost = new Post();
        repost.setContent(original.getContent());
        repost.setUser(user);
        repost.setRepost(true);
        repost.setOriginalPost(original);
        repost.setPromotional(original.isPromotional());
        repost.setCtaLink(original.getCtaLink());

        return PostMapper.toDto(postRepository.save(repost));
    }
}