package com.example.demo.controller;

import com.example.demo.dto.ProfileResponse;
import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.exception.CustomException;
import com.example.demo.service.ProfileService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // ============================================================
    // ================= VIEW MY PROFILE ==========================
    // ============================================================
    @GetMapping("/me")
    public String getMyProfile(Authentication authentication, Model model) {

        ProfileResponse profile =
                profileService.getMyProfile(authentication.getName());

        model.addAttribute("profile", profile);
        model.addAttribute("loggedUsername", authentication.getName());

        return "profile";
    }

    // ============================================================
    // ================= VIEW OTHER USER PROFILE ==================
    // ============================================================
    @GetMapping("/{username}")
    public String viewProfile(@PathVariable String username,
                              Authentication authentication,
                              Model model) {

        String loggedUser = authentication != null
                ? authentication.getName()
                : null;

        ProfileResponse profile =
                profileService.getProfile(username, loggedUser);

        model.addAttribute("profile", profile);
        model.addAttribute("loggedUsername", loggedUser);

        return "profile";
    }

    // ============================================================
    // =========== VIEW OTHER PROFILE (FORM REDIRECT) ============
    // ============================================================
    // This prevents path conflict issues
    @GetMapping("/view")
    public String viewProfileByForm(@RequestParam String username) {

        return "redirect:/api/profile/" + username;
    }

    // ============================================================
    // ================= SHOW EDIT PAGE ===========================
    // ============================================================
    @GetMapping("/edit")
    public String editProfilePage(Authentication authentication, Model model) {

        ProfileResponse profile =
                profileService.getMyProfile(authentication.getName());

        model.addAttribute("profile", profile);

        return "edit-profile";
    }

    // ============================================================
    // ================= SEARCH USERS =============================
    // ============================================================
    @GetMapping("/search")
    public String searchUsers(@RequestParam String keyword,
                              Authentication authentication,
                              Model model) {

        model.addAttribute("results",
                profileService.searchUsers(keyword));

        model.addAttribute("loggedUsername",
                authentication.getName());

        return "profile-search";
    }

    // ============================================================
    // ================= HANDLE EDIT SUBMIT =======================
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
                                Model model) {

        try {

            ProfileResponse existingProfile =
                    profileService.getMyProfile(authentication.getName());

            String profileImagePath = existingProfile.profilePicture();
            String bannerImagePath = existingProfile.bannerImage();

            String uploadDir = System.getProperty("user.dir") + "/uploads/";
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
                    request.fullName(),
                    request.bio(),
                    profileImagePath,
                    request.location(),
                    request.website(),
                    request.category(),
                    request.contactInfo(),
                    request.businessAddress(),
                    request.businessHours(),
                    request.isPrivate(),
                    request.username(),
                    bannerImagePath
            );

            profileService.updateProfile(authentication.getName(), updatedRequest);

            return "redirect:/api/profile/me";

        } catch (CustomException e) {

            ProfileResponse profile =
                    profileService.getMyProfile(authentication.getName());

            model.addAttribute("profile", profile);
            model.addAttribute("error", e.getMessage());

            return "edit-profile";

        } catch (IOException e) {

            ProfileResponse profile =
                    profileService.getMyProfile(authentication.getName());

            model.addAttribute("profile", profile);
            model.addAttribute("error", "File upload failed. Try again.");

            return "edit-profile";
        }
    }
}