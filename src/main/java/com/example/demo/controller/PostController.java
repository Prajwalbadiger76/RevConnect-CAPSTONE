package com.example.demo.controller;

import com.example.demo.dto.PostDto;
<<<<<<< Updated upstream
import com.example.demo.dto.PostRequestDTO;
import com.example.demo.service.PostService;

import org.springframework.beans.factory.annotation.Autowired;
=======
import com.example.demo.dto.ProfileResponse;
import com.example.demo.entity.Post;
import com.example.demo.repo.PostRepository;
import com.example.demo.service.PostService;
import java.util.List;
import org.springframework.security.core.Authentication;
>>>>>>> Stashed changes
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

   
    // Show Create Form
   
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("postRequestDTO", new PostRequestDTO());
        return "create-post";
    }

   
    // Create Post
 
    @PostMapping("/create")
<<<<<<< Updated upstream
    public String createPost(@ModelAttribute PostRequestDTO dto) {

        Long currentUserId = 1L; // Replace with JWT later
        postService.createPost(dto, currentUserId);

        return "redirect:/posts/feed";
=======
    public String createPost(@RequestParam String content,
                             @RequestParam(required = false) String hashtags,
                             Authentication authentication) {

        String fullContent = content;

        if (hashtags != null && !hashtags.isBlank()) {
            fullContent = content + " " + hashtags;
        }

        postService.createPost(authentication.getName(), fullContent);

        return "redirect:/feed";
>>>>>>> Stashed changes
    }


    // View Feed
   
    @GetMapping("/feed")
    public String viewFeed(Model model) {

        List<PostDto> posts = postService.getFeed();
        model.addAttribute("posts", posts);

        return "feed";
    }

   
    // Update Post
   
    @PostMapping("/update/{id}")
    public String updatePost(@PathVariable Long id,
                             @ModelAttribute PostRequestDTO dto) {

        Long currentUserId = 1L;
        postService.updatePost(id, dto, currentUserId);

        return "redirect:/posts/feed";
    }

  
    // Delete Post
  
    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {

        Long currentUserId = 1L;
        postService.deletePost(id, currentUserId);

        return "redirect:/posts/feed";
    }

    // Pin Post
  
    @PostMapping("/pin/{id}")
    public String pinPost(@PathVariable Long id) {

        Long currentUserId = 1L;
        postService.pinPost(id, currentUserId);

        return "redirect:/posts/feed";
    }

   
    // Repost
    @PostMapping("/repost/{id}")
    public String repost(@PathVariable Long id) {

        Long currentUserId = 1L;
        postService.repostPost(id, currentUserId);

        return "redirect:/posts/feed";
    }
    
    //=============PIN===================
    
    @PostMapping("/pin/{id}")
    public String pinPost(@PathVariable Long id,
                          Authentication authentication) {

        postService.pinPost(id, authentication.getName());
        return "redirect:/profile/" + authentication.getName();
    }
    
    @PostMapping("/unpin/{id}")
    public String unpinPost(@PathVariable Long id,
                            Authentication authentication) {

        postService.unpinPost(id, authentication.getName());
        return "redirect:/profile/" + authentication.getName();
    }
   
    
   
}