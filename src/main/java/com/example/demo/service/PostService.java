package com.example.demo.service;

import com.example.demo.dto.PostDto;
import com.example.demo.dto.PostRequestDTO;
import java.util.List;

public interface PostService {

    PostDto createPost(PostRequestDTO dto, Long userId);

    PostDto updatePost(Long postId, PostRequestDTO dto, Long userId);

    void deletePost(Long postId, Long userId);

    List<PostDto> getFeed();

<<<<<<< Updated upstream
    PostDto pinPost(Long postId, Long userId);

    PostDto repostPost(Long postId, Long userId);
=======
    void deletePost(Long postId, String username);
    
    void sharePost(Long id, String username);
    
    void pinPost(Long postId, String username);

    void unpinPost(Long postId, String username);

    List<PostDto> searchByHashtag(String hashtag);
>>>>>>> Stashed changes
}