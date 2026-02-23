package com.example.demo.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.dto.PostRequestDTO;
import com.example.demo.entity.Posts;
import com.example.demo.repository.PostRepository;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedActionException;

@Service
public class PostServiceImpl implements PostService {
	
	@Autowired
	private PostRepository postRepository;
	
	@Override
	public Posts savePost(PostRequestDTO dto, Long userId) {
		Posts post = new Posts();
		post.setContent(dto.getContent());
		post.setHashtags(dto.getHashtags());
		post.setUserId(userId);
		post.setPromotional(dto.isPromotional());
		post.setCtaLink(dto.getCtaLink());
		post.setScheduledTime(dto.getScheduledTime());
		return postRepository.save(post);
	}

	@Override
	public void deletePost(Long postId, Long userId) {
		Posts post = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post not found"));
		
		if (!post.getUserId().equals(userId)) {
			throw new UnauthorizedActionException("You are not allowed to delete this post");
		}
		postRepository.delete(post);
	}

	@Override
	public List<Posts> getUserFeed(Long userId) {
		return postRepository.findByUserIdOrderByCreatedAtDesc(userId);
	}
}