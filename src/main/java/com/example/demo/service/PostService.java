package com.example.demo.service;

import com.example.demo.dto.PostRequestDTO;
import com.example.demo.entity.Posts;
import java.util.List;




public interface PostService {
	Posts savePost(PostRequestDTO dto,Long userId);
	void deletePost(Long postId, Long userId);
	List<Posts> getUserFeed(Long userId);

}
