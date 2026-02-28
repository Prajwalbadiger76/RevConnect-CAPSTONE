package com.example.demo.service;

import com.example.demo.dto.PostDto;
import com.example.demo.entity.Post;

import java.util.List;

public interface PostService {

	void createPost(String username,
            String content,
            String hashtags,
            String scheduledAt);

    void updatePost(Long postId, String content, String hashtags, String username);

    void deletePost(Long postId, String username);

   // void sharePost(Long postId, String username);

    void toggleLike(Long postId, String username);

    void pinPost(Long postId, String username);

    void unpinPost(Long postId, String username);

   
    List<PostDto> getPostsByUsername(String username, String currentUser);

    List<PostDto> getAllPosts();

    List<PostDto> getFeedPosts(String username);

    // ADD THIS (needed for edit page)
    PostDto getPostById(Long postId, String currentUsername);

    // ADD THIS (needed for hashtag search)
    List<PostDto> searchPostsByHashtag(String tag,String username);
    
    void sharePost(Long id, String username);

    List<String> getTrendingHashtags();
    

    List<PostDto> searchPostsByContent(String keyword, String username);

}
