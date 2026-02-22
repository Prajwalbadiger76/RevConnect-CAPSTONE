package com.example.demo.service;

import com.example.demo.dto.ProfileResponse;
import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.entity.User;
import com.example.demo.exception.CustomException;
import com.example.demo.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;

    public ProfileServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ============================================================
    // ================= GET MY PROFILE ===========================
    // ============================================================
    @Override
    public ProfileResponse getMyProfile(String username) {
        User user = findUser(username);
        return map(user, false);
    }

    // ============================================================
    // ================= GET PUBLIC PROFILE =======================
    // ============================================================
    @Override
    public ProfileResponse getProfile(String username, String viewerUsername) {

        User profileUser = findUser(username);

        boolean isOwner = profileUser.getUsername().equals(viewerUsername);
        boolean isFollower = false; // future integration

        if (profileUser.isPrivate() && !isOwner && !isFollower) {
            return map(profileUser, true);
        }

        return map(profileUser, false);
    }

    // ============================================================
    // ================= UPDATE PROFILE ===========================
    // ============================================================
    @Override
    public ProfileResponse updateProfile(String username, UpdateProfileRequest request) {

        User user = findUser(username);

        // ================= USERNAME VALIDATION =================
        if (request.username() != null &&
                !request.username().isBlank() &&
                !request.username().equals(user.getUsername())) {

            if (userRepository.existsByUsername(request.username())) {
                throw new CustomException("Username already taken");
            }

            user.setUsername(request.username());
        }

        // ================= HANDLE EMPTY STRINGS =================
        user.setFullName(clean(request.fullName()));
        user.setBio(clean(request.bio()));
        user.setLocation(clean(request.location()));
        user.setWebsite(clean(request.website()));
        user.setCategory(clean(request.category()));
        user.setContactInfo(clean(request.contactInfo()));
        user.setBusinessAddress(clean(request.businessAddress()));
        user.setBusinessHours(clean(request.businessHours()));

        // Images
        if (request.profilePicture() != null)
            user.setProfilePicture(request.profilePicture());

        if (request.bannerImage() != null)
            user.setBannerImage(request.bannerImage());

        if (request.isPrivate() != null)
            user.setPrivate(request.isPrivate());

        userRepository.save(user);

        return map(user, false);
    }

    // ============================================================
    // ================= SEARCH USERS =============================
    // ============================================================
    @Override
    public List<ProfileResponse> searchUsers(String keyword) {
        return userRepository
                .findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(
                        keyword, keyword)
                .stream()
                .map(user -> map(user, false))
                .toList();
    }

    // ============================================================
    // ================= HELPER: CLEAN STRING =====================
    // Converts "" → null
    // ============================================================
    private String clean(String value) {
        if (value == null) return null;
        return value.isBlank() ? null : value;
    }

    // ============================================================
    // ================= FIND USER ================================
    // ============================================================
    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("User not found"));
    }

    // ============================================================
    // ================= MAP ENTITY → DTO =========================
    // ============================================================
    private ProfileResponse map(User user, boolean limitedView) {

        // ================= PROFILE COMPLETION =================
        int totalFields = 8;
        int filled = 0;

        if (user.getFullName() != null) filled++;
        if (user.getBio() != null) filled++;
        if (user.getProfilePicture() != null) filled++;
        if (user.getLocation() != null) filled++;
        if (user.getWebsite() != null) filled++;
        if (user.getCategory() != null) filled++;
        if (user.getContactInfo() != null) filled++;
        if (user.getBusinessAddress() != null) filled++;

        int percentage = (filled * 100) / totalFields;

        long postCount = 0;
        long followerCount = 0;
        long followingCount = 0;

        String bio = limitedView ? null : user.getBio();
        String location = limitedView ? null : user.getLocation();
        String website = limitedView ? null : user.getWebsite();
        String category = limitedView ? null : user.getCategory();
        String contactInfo = limitedView ? null : user.getContactInfo();
        String businessAddress = limitedView ? null : user.getBusinessAddress();
        String businessHours = limitedView ? null : user.getBusinessHours();

        return new ProfileResponse(
                user.getUsername(),
                user.getEmail(),
                user.getRole(),

                user.getFullName(),
                bio,
                user.getProfilePicture(),
                user.getBannerImage(),

                location,
                website,
                user.isPrivate(),

                category,
                contactInfo,
                businessAddress,
                businessHours,

                percentage,

                postCount,
                followerCount,
                followingCount,

                limitedView
        );
    }
}