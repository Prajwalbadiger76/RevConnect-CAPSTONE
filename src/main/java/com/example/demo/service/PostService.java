package com.example.demo.service;

import com.example.demo.dto.PostRequestDTO;
import com.example.demo.entity.Post;
import java.util.List;


public interface PostService {
	Post savePost(PostRequestDTO dto,Long userId);
	void deletePost(Long postId, Long userId);
	List<Post> getUserFeed(Long userId);

}
