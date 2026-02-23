package com.example.demo.service;

import com.example.demo.dto.PostDto;
import com.example.demo.dto.PostRequestDTO;
import java.util.List;

public interface PostService {

    PostDto createPost(PostRequestDTO dto, Long userId);

    PostDto updatePost(Long postId, PostRequestDTO dto, Long userId);

    void deletePost(Long postId, Long userId);

    List<PostDto> getFeed();

    PostDto pinPost(Long postId, Long userId);

    PostDto repostPost(Long postId, Long userId);
}