package com.example.demo.controller;

import com.example.demo.dto.FollowerGrowthDTO;
import com.example.demo.dto.PostDto;
import com.example.demo.dto.ProfileResponse;
import com.example.demo.dto.ProfileWithFollowResponse;
import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.service.AnalyticsService;
import com.example.demo.service.ConnectionService;
import com.example.demo.service.PostService;
import com.example.demo.service.ProfileService;
import com.example.demo.service.UserService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final ConnectionService connectionService;
    private final UserService userService;
    private final AnalyticsService analyticsService;
    private final  PostService postService;


    public ProfileController(ProfileService profileService,
            ConnectionService connectionService,
            UserService userService,
            AnalyticsService analyticsService,PostService postService) {
this.profileService = profileService;
this.connectionService = connectionService;
this.userService = userService;
this.analyticsService = analyticsService;
this.postService = postService;  

}

    // ============================================================
    // ================= VIEW MY PROFILE ==========================
    // ============================================================
    @GetMapping
	public String getMyProfile(Authentication authentication,
	                           Model model,
	                           @RequestParam(defaultValue = "posts") String tab,
	                           @RequestParam(value = "error", required = false) String error) {

	    if (authentication == null) {
	        return "redirect:/login";
	    }

	    String currentUsername = authentication.getName();

	    ProfileResponse profile =
	            profileService.getProfileWithFollowInfo(
	                    currentUsername,
	                    currentUsername
	            );

	    model.addAttribute("profile", profile);
	    model.addAttribute("isOwner", true);
	    model.addAttribute("canView", true);

	    // FETCH POSTS
	    List<PostDto> posts =
	            postService.getPostsByUsername(currentUsername, currentUsername);

	    model.addAttribute("posts", posts);   
	    model.addAttribute("tab", tab); 

	    model.addAttribute("connectionCount",
	            connectionService.getConnectionCount(currentUsername));

	    if (error != null) {
	        model.addAttribute("searchError", "No users found.");
	    }

	    return "profile";
	}

    // ============================================================
    // ================= VIEW OTHER PROFILE =======================
    // ============================================================
    @GetMapping("/{username}")
    public String viewProfile(@PathVariable String username,
                              @RequestParam(defaultValue = "posts") String tab,
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

        // ✅ NOW tab exists
        model.addAttribute("tab", tab);

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

        // 🔥 LOAD POSTS
        List<PostDto> posts = Collections.emptyList();

        if (canView) {
            posts = postService.getPostsByUsername(username, currentUsername);
        }

        model.addAttribute("posts", posts);

        return "profile";
    }

    // ============================================================
    // ================= EDIT PROFILE PAGE ========================
    // ============================================================
    @GetMapping("/edit")
    public String editProfilePage(Authentication authentication,
                                  Model model) {

        if (authentication == null) {
            return "redirect:/login";
        }

        ProfileResponse profile =
                profileService.getMyProfile(authentication.getName());

        model.addAttribute("profile", profile);
        return "edit-profile";
    }

    // ============================================================
    // ================= UPDATE PROFILE ===========================
    // ============================================================
    @PostMapping("/edit")
    public String updateProfile(Authentication authentication,
                                @ModelAttribute UpdateProfileRequest request,
                                @RequestParam(value = "profileImage", required = false)
                                MultipartFile profileImage,
                                @RequestParam(value = "bannerImageFile", required = false)
                                MultipartFile bannerImageFile,
                                @RequestParam(value = "removeProfileImage", required = false)
                                String removeProfileImage,
                                RedirectAttributes redirectAttributes) {

        if (authentication == null) {
            return "redirect:/login";
        }

        try {

            ProfileResponse existingProfile =
                    profileService.getMyProfile(authentication.getName());

            String profileImagePath = existingProfile.profilePicture();
            String bannerImagePath = existingProfile.bannerImage();

            String uploadDir = "uploads/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            if (removeProfileImage != null) {
                profileImagePath = null;
            }

            if (profileImage != null && !profileImage.isEmpty()) {

                String fileName = System.currentTimeMillis() + "_" +
                        profileImage.getOriginalFilename();

                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, profileImage.getBytes());

                profileImagePath = "/uploads/" + fileName;
            }

            if (bannerImageFile != null && !bannerImageFile.isEmpty()) {

                String fileName = System.currentTimeMillis() + "_" +
                        bannerImageFile.getOriginalFilename();

                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, bannerImageFile.getBytes());

                bannerImagePath = "/uploads/" + fileName;
            }

            UpdateProfileRequest updatedRequest = new UpdateProfileRequest(
                    request.username(),
                    request.email(),
                    request.role(),
                    request.fullName(),
                    request.bio(),
                    profileImagePath,
                    bannerImagePath,
                    request.location(),
                    request.website(),
                    request.isPrivate(),
                    request.creatorName(),
                    request.industry(),
                    request.instagramLink(),
                    request.youtubeLink(),
                    request.twitterLink(),
                    request.portfolioLink(),
                    request.skills(),
                    request.businessName(),
                    request.category(),
                    request.contactInfo(),
                    request.businessAddress(),
                    request.businessHours(),
                    request.businessDescription(),
                    request.servicesOffered(),
                    request.mapLocationLink()
            );

            profileService.updateProfile(authentication.getName(), updatedRequest);

            redirectAttributes.addFlashAttribute("success",
                    "Profile updated successfully!");

            // ✅ FIXED redirect
            return "redirect:/profile";

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("error",
                    "Update failed.");

            return "redirect:/profile/edit";
        }
    }

    // ============================================================
    // ================= SEARCH USERS =============================
    // ============================================================
    @GetMapping("/search")
    public String searchUsers(@RequestParam String keyword,
                              Authentication authentication,
                              Model model) {

        if (authentication == null) {
            return "redirect:/login";
        }

        model.addAttribute("results",
                profileService.searchUsers(keyword));

        model.addAttribute("currentUsername",
                authentication.getName());

        return "search-results";
    }
}