package com.example.demo.service;

import com.example.demo.dto.ProfileResponse;
import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.CustomException;
import com.example.demo.repo.UserRepository;
import com.example.demo.repo.FollowRepository;
import com.example.demo.repo.PostRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {

    private static final Logger logger =
            LogManager.getLogger(ProfileServiceImpl.class);

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;

    
    public ProfileServiceImpl(UserRepository userRepository, FollowRepository followRepository, PostRepository postRepository) {
            this.userRepository = userRepository; 
            this.followRepository = followRepository;
            this.postRepository = postRepository;
    }

    
    @Override
    public ProfileResponse getProfile(String username, String viewerUsername) {

        logger.info("Profile view request: {} viewing {}", viewerUsername, username);

        User profileUser = findUser(username);

        boolean isOwner = viewerUsername != null &&
                profileUser.getUsername().equals(viewerUsername);

        boolean isFollower = false; // future integration

        if (profileUser.isPrivate() && !isOwner && !isFollower) {
            return map(profileUser, true);
        }

        return map(profileUser, false);
    }
    
    
 // ================= GET MY PROFILE =================
    @Override
    public ProfileResponse getMyProfile(String username) {

        logger.info("Fetching own profile for user: {}", username);

        User user = findUser(username);

        // own profile → never limited view
        return map(user, false);
    }
    // ================= VIEW PROFILE WITH FOLLOW INFO =================
    
    @Override
    public ProfileResponse getProfileWithFollowInfo(
            String currentUsername,
            String targetUsername) {

        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow();
        User targetUser = userRepository.findByUsername(targetUsername).orElseThrow();

        boolean isOwn = currentUsername.equals(targetUsername);

        boolean isFollowing = false;
        if (!isOwn) {
            isFollowing = followRepository
                    .findByFollowerAndFollowing(currentUser, targetUser)
                    .isPresent();
        }

        long followerCount = followRepository.countByFollowing(targetUser);
        long followingCount = followRepository.countByFollower(targetUser);

//        long postCount = postRepository.countByUser(targetUser);
        long postCount = 0;

        //  Privacy Logic
        boolean limitedView = false;
        String privateMessage = null;

        if (Boolean.TRUE.equals(targetUser.isPrivate()) && !isOwn && !isFollowing) {
            limitedView = true;
            privateMessage = "This account is private. Follow to see full profile.";
        }

        //  Completion Percentage (simple example)
        int completionPercentage = calculateCompletionPercentage(targetUser);

        return new ProfileResponse(

                // BASIC
                targetUser.getUsername(),
                targetUser.getEmail(),
                targetUser.getRole(),
                targetUser.getFullName(),
                targetUser.getBio(),
                targetUser.getProfilePicture(),
                targetUser.getBannerImage(),
                targetUser.getLocation(),
                targetUser.getWebsite(),
                targetUser.isPrivate(),

                // CREATOR
                targetUser.getCreatorName(),
                targetUser.getIndustry(),
                targetUser.getInstagramLink(),
                targetUser.getYoutubeLink(),
                targetUser.getTwitterLink(),
                targetUser.getPortfolioLink(),
                targetUser.getSkills(),

                // BUSINESS
                targetUser.getBusinessName(),
                targetUser.getCategory(),
                targetUser.getContactInfo(),
                targetUser.getBusinessAddress(),
                targetUser.getBusinessHours(),
                targetUser.getBusinessDescription(),
                targetUser.getServicesOffered(),
                targetUser.getMapLocationLink(),

                // STATS
                completionPercentage,
                postCount,
                followerCount,
                followingCount,

                // PRIVACY
                limitedView,
                privateMessage,

                // FOLLOW INFO
                isFollowing,
                isOwn
        );
    }
    
    private int calculateCompletionPercentage(User user) {

        int totalFields = 8;
        int filled = 0;

        if (user.getFullName() != null) filled++;
        if (user.getBio() != null) filled++;
        if (user.getProfilePicture() != null) filled++;
        if (user.getLocation() != null) filled++;
        if (user.getWebsite() != null) filled++;
        if (user.getSkills() != null) filled++;
        if (user.getBusinessName() != null) filled++;
        if (user.getCategory() != null) filled++;

        return (filled * 100) / totalFields;
    }

    // ================= UPDATE PROFILE =================
    @Override
    public ProfileResponse updateProfile(String username, UpdateProfileRequest request) {

        logger.info("Updating profile for user: {}", username);

        User user = findUser(username);

        // ================= ROLE UPDATE =================
        if (request.role() != null) {
            user.setRole(request.role());
        }

        // ================= USERNAME UPDATE =================
        if (request.username() != null &&
                !request.username().isBlank() &&
                !request.username().equals(user.getUsername())) {

            if (userRepository.existsByUsername(request.username())) {
                throw new CustomException("Username already taken");
            }

            user.setUsername(request.username());
        }

        // ================= BASIC FIELDS =================
        updateIfNotNull(user::setFullName, request.fullName());
        updateIfNotNull(user::setBio, request.bio());
        updateIfNotNull(user::setLocation, request.location());
        updateIfNotNull(user::setWebsite, request.website());

        updateIfNotNull(user::setProfilePicture, request.profilePicture());
        updateIfNotNull(user::setBannerImage, request.bannerImage());

        if (request.isPrivate() != null) {
            user.setPrivate(request.isPrivate());
        }

        // ==================================================
        // ================= ROLE BASED LOGIC =================
        // ==================================================

        if (user.getRole() == Role.CREATOR) {

            updateIfNotNull(user::setCreatorName, request.creatorName());
            updateIfNotNull(user::setIndustry, request.industry());
            updateIfNotNull(user::setInstagramLink, request.instagramLink());
            updateIfNotNull(user::setYoutubeLink, request.youtubeLink());
            updateIfNotNull(user::setTwitterLink, request.twitterLink());
            updateIfNotNull(user::setPortfolioLink, request.portfolioLink());
            updateIfNotNull(user::setSkills, request.skills());

            clearBusinessFields(user);

        } else if (user.getRole() == Role.BUSINESS) {

            updateIfNotNull(user::setBusinessName, request.businessName());
            updateIfNotNull(user::setCategory, request.category());
            updateIfNotNull(user::setContactInfo, request.contactInfo());
            updateIfNotNull(user::setBusinessAddress, request.businessAddress());
            updateIfNotNull(user::setBusinessHours, request.businessHours());
            updateIfNotNull(user::setBusinessDescription, request.businessDescription());
            updateIfNotNull(user::setServicesOffered, request.servicesOffered());
            updateIfNotNull(user::setMapLocationLink, request.mapLocationLink());

            clearCreatorFields(user);

        } else if (user.getRole() == Role.PERSONAL) {

            clearCreatorFields(user);
            clearBusinessFields(user);
        }

        userRepository.save(user);

        return map(user, false);
    }
    
    private void updateIfNotNull(java.util.function.Consumer<String> setter, String value) {
        if (value != null) {
            setter.accept(value.isBlank() ? null : value);
        }
    }

    private void clearCreatorFields(User user) {
        user.setCreatorName(null);
        user.setIndustry(null);
        user.setInstagramLink(null);
        user.setYoutubeLink(null);
        user.setTwitterLink(null);
        user.setPortfolioLink(null);
        user.setSkills(null);
    }

    private void clearBusinessFields(User user) {
        user.setBusinessName(null);
        user.setCategory(null);
        user.setContactInfo(null);
        user.setBusinessAddress(null);
        user.setBusinessHours(null);
        user.setBusinessDescription(null);
        user.setServicesOffered(null);
        user.setMapLocationLink(null);
    }

    // ================= SEARCH USERS =================
    @Override
    public List<ProfileResponse> searchUsers(String keyword) {

        return userRepository
                .findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(
                        keyword, keyword)
                .stream()
                .map(user -> map(user, false))
                .toList();
    }

    // ================= HELPER =================
    private String clean(String value) {
        if (value == null || value.isBlank()) return null;
        return value;
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("User not found"));
    }

    // ================= PROFILE MAPPER =================
    private ProfileResponse map(User user, boolean limitedView) {

        boolean isBusiness =
                user.getRole() != null &&
                user.getRole().name().equals("BUSINESS");

        // ===== PROFILE COMPLETION =====
        int score = 0;

        if (user.getFullName() != null) score += 15;
        if (user.getBio() != null) score += 15;
        if (user.getProfilePicture() != null) score += 20;
        if (user.getBannerImage() != null) score += 10;
        if (user.getLocation() != null) score += 10;
        if (user.getWebsite() != null) score += 10;

        if (isBusiness) {
            if (user.getCategory() != null) score += 5;
            if (user.getContactInfo() != null) score += 5;
            if (user.getBusinessAddress() != null) score += 5;
            if (user.getBusinessHours() != null) score += 5;
        }

        int percentage = Math.min(score, 100);

        // ===== Limited View Handling =====
        String bio = limitedView ? null : user.getBio();
        String location = limitedView ? null : user.getLocation();
        String website = limitedView ? null : user.getWebsite();

        String category = (!limitedView && isBusiness) ? user.getCategory() : null;
        String contactInfo = (!limitedView && isBusiness) ? user.getContactInfo() : null;
        String businessAddress = (!limitedView && isBusiness) ? user.getBusinessAddress() : null;
        String businessHours = (!limitedView && isBusiness) ? user.getBusinessHours() : null;

        long postCount = 0;
        long followerCount = 0;
        long followingCount = 0;

        return new ProfileResponse(

                // BASIC
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

                // CREATOR
                user.getCreatorName(),
                user.getIndustry(),
                user.getInstagramLink(),
                user.getYoutubeLink(),
                user.getTwitterLink(),
                user.getPortfolioLink(),
                user.getSkills(),

                // BUSINESS
                user.getBusinessName(),
                category,
                contactInfo,
                businessAddress,
                businessHours,
                user.getBusinessDescription(),
                user.getServicesOffered(),
                user.getMapLocationLink(),

                // STATS
                percentage,
                postCount,
                followerCount,
                followingCount,

                limitedView,
                limitedView ? "This account is private. Follow to see full details." : null,

                //  ADD THESE TWO
                false,
                false
        );
    }



}