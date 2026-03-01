package com.example.demo.controller;

import com.example.demo.dto.PostDto;
import com.example.demo.entity.*;

import com.example.demo.service.*;

import java.security.Principal;
import java.util.*;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@Controller
@RequestMapping("/post")   // IMPORTANT: base path
public class PostController {

    private final PostService postService;
    private final ProfileService profileService;
   
   

    public PostController(PostService postService,
            ProfileService profileService) {
					this.postService = postService;
					this.profileService = profileService;
					}

    // =========================
    // SHOW CREATE PAGE (GET)
    // =========================
    @GetMapping("/create")
    public String showCreatePostPage() {
        return "create-post";
    }

    // =========================
    // CREATE POST (POST)
    // =========================
    @PostMapping("/create")
    public String createPost(@RequestParam String content,
                             @RequestParam(required = false) String hashtags,
                             @RequestParam(required = false) String scheduledAt,
                             Authentication authentication) {

        postService.createPost(
                authentication.getName(),
                content,
                hashtags,
                scheduledAt
        );

        return "redirect:/feed";
    }

    // =========================
    // SHOW EDIT PAGE (GET)
 // =========================
 // SHOW EDIT PAGE (GET)
 // =========================
 @GetMapping("/edit/{id}")
 public String editPostPage(@PathVariable Long id,
                            Authentication authentication,
                            Model model) {

     PostDto post = postService.getPostById(id, authentication.getName());
     model.addAttribute("post", post);

     return "edit-post";
 }
    public String showEditPage(@PathVariable Long id,
                               Model model,
                               Authentication authentication) {

        PostDto post = postService.getPostById(id, authentication.getName());
        model.addAttribute("post", post);

        return "edit-post";
    }

    // =========================
    // UPDATE POST (POST)
    // =========================
    @PostMapping("/update/{id}")
    public String updatePost(@PathVariable Long id,
                             @RequestParam String content,
                             @RequestParam(required = false) String hashtags,
                             Authentication authentication) {

        postService.updatePost(id, content, hashtags, authentication.getName());
        return "redirect:/feed";
    }

    // =========================
    // DELETE POST
    // =========================
    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id,
                             Principal principal) {

        postService.deletePost(id, principal.getName());

        return "redirect:/profile";
    }

    // =========================
    // PIN POST
    // =========================
    @PostMapping("/pin/{id}")
    public String pinPost(@PathVariable Long id,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {

        try {
            postService.pinPost(id, authentication.getName());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error",
                    "You can only pin maximum 3 posts.");
        }

        return "redirect:/profile/" + authentication.getName() + "?tab=posts";
    }
    // =========================
    // UNPIN POST
    // =========================
    @PostMapping("/unpin/{id}")
    public String unpinPost(@PathVariable Long id,
                            Authentication authentication) {

        postService.unpinPost(id, authentication.getName());

        return "redirect:/profile/" + authentication.getName() + "?tab=posts";
    }

    // =========================
    // SHARE POST
    // =========================
    @PostMapping("/share/{id}")
    public String sharePost(@PathVariable Long id,
                            Authentication authentication) {

        postService.sharePost(id, authentication.getName());

        return "redirect:/feed";
    }

    // =========================
    // SEARCH POSTS BY HASHTAG
    // =========================
    @GetMapping("/search")
    public String search(@RequestParam String keyword,
                         Model model,
                         Authentication authentication) {

        String currentUsername = authentication.getName();
        keyword = keyword.trim();

        List<PostDto> posts = new ArrayList<>();
        List<?> users = new ArrayList<>();

        if (keyword.startsWith("#")) {

            String tag = keyword.substring(1).toLowerCase();
            posts = postService.searchPostsByHashtag(tag, currentUsername);

            model.addAttribute("searchType", "hashtag");
            model.addAttribute("searchedKeyword", keyword);

        } else {

            users = profileService.searchUsers(keyword);
            model.addAttribute("searchType", "user");
            model.addAttribute("searchedKeyword", keyword);
        }

        model.addAttribute("posts", posts);
        model.addAttribute("results", users);
        model.addAttribute("currentUsername", currentUsername);
        model.addAttribute("trendingHashtags",
                postService.getTrendingHashtags());
        model.addAttribute("showCreateForm", false);

        return "search-results";
    }
    
    @GetMapping("/trending")
    public String trendingPage(
            @RequestParam(required = false) String tag,
            Model model,
            Authentication authentication) {

        String username = authentication.getName();

        List<String> trendingTags =
                postService.getTrendingHashtags();

        model.addAttribute("trendingHashtags", trendingTags);
        model.addAttribute("currentUsername", username);

        // If no tag selected → default to first trending tag
        if (tag == null && !trendingTags.isEmpty()) {
            tag = trendingTags.get(0);
        }

        if (tag != null) {
            List<PostDto> posts =
                    postService.searchPostsByHashtag(tag, username);

            model.addAttribute("posts", posts);
            model.addAttribute("selectedTag", tag);
        }

        model.addAttribute("showCreateForm", false);

        return "trending";
    }
    
    
}