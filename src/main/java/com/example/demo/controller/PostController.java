package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import com.example.demo.dto.PostRequestDTO;
import com.example.demo.entity.Posts;
import com.example.demo.service.PostService;

@Controller
@RequestMapping("/posts")
public class PostController {
	
	@Autowired
	private PostService postService;
	
	@GetMapping("/create")
	public String showCreateForm(Model model) {
		model.addAttribute("postDTO",new PostRequestDTO());
		return "post-create";
	}
	
	
	@GetMapping("/feed") // Change to GetMapping and use the actual URL
	public String viewFeed(Model model) {
	    Long currentUserId = 1L;
	    List<Posts> posts = postService.getUserFeed(currentUserId);
	    model.addAttribute("posts", posts);
	    return "post-feed"; // This returns the post-feed.html file
	}

}
