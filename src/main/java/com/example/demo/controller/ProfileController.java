package com.example.demo.controller;

import com.example.demo.dto.ProfileResponse;
import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.service.ConnectionService;
import com.example.demo.service.PostService;
import com.example.demo.service.ProfileService;
import com.example.demo.service.UserService;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
public class ProfileController {

	private final ProfileService profileService;
	private final ConnectionService connectionService;
	private final UserService userService;
	private final  PostService postService;


	public ProfileController(ProfileService profileService,
            ConnectionService connectionService,
            UserService userService,
            PostService postService) {

this.profileService = profileService;
this.connectionService = connectionService;
this.userService = userService;
this.postService = postService;   // ✅ IMPORTANT
}

    // ================= VIEW MY PROFILE =================
    @GetMapping
    public String getMyProfile(Authentication authentication, Model model) {

        String currentUsername = authentication.getName();

        ProfileResponse profile =
                profileService.getProfileWithFollowInfo(
                        currentUsername,
                        currentUsername
                );

        model.addAttribute("profile", profile);

        // ✅ Add connection count
        model.addAttribute("connectionCount",
                connectionService.getConnectionCount(currentUsername));

        return "profile";
    }

    // ================= VIEW OTHER PROFILE =================
    @GetMapping("/{username}")
    public String viewProfile(@PathVariable String username,
                              Authentication authentication,
                              Model model) {

        String currentUsername = authentication.getName();

        ProfileResponse profile =
                profileService.getProfileWithFollowInfo(
                        currentUsername,
                        username
                );

        boolean isOwner = currentUsername.equals(username);

        model.addAttribute("profile", profile);
        model.addAttribute("isOwner", isOwner);

        boolean canView =
                Boolean.FALSE.equals(profile.isPrivate())
                || profile.isFollowing()
                || profile.isOwn();

        model.addAttribute("canView", canView);

        model.addAttribute("connectionStatus",
                connectionService.getConnectionStatus(
                        currentUsername,
                        username
                ));

        model.addAttribute("connectionCount",
                connectionService.getConnectionCount(username));

        Long pendingRequestId =
                connectionService.getPendingRequestId(
                        currentUsername,
                        username
                );

        model.addAttribute("pendingRequestId", pendingRequestId);

        // 🔥 ADD THIS
        List<PostDto> posts =
                postService.getUserPosts(username, currentUsername);

        model.addAttribute("posts", posts);

        return "profile";
    }

    // ================= EDIT PROFILE PAGE =================
    @GetMapping("/edit")
    public String editProfilePage(Authentication authentication, Model model) {

        ProfileResponse profile =
                profileService.getMyProfile(authentication.getName());

        model.addAttribute("profile", profile);

        return "edit-profile";
    }

    // ================= UPDATE PROFILE =================
    @PostMapping("/edit")
    public String updateProfile(Authentication authentication,
                                @ModelAttribute UpdateProfileRequest request) {

        profileService.updateProfile(authentication.getName(), request);

        return "redirect:/profile";
    }

    // ================= SEARCH USERS =================
    
    @GetMapping("/search")
    public String searchUsers(@RequestParam String keyword,
                              Authentication authentication,
                              Model model) {

        String currentUsername = authentication.getName();

        model.addAttribute("results",
                profileService.searchUsers(keyword));

        model.addAttribute("currentUsername", currentUsername);
        
        model.addAttribute("showCreateForm", false);

        return "feed"; // Only users
    }
}
