package com.example.demo.service;

import com.example.demo.dto.ProfileResponse;
import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.CustomException;
import com.example.demo.repo.FollowRepository;
import com.example.demo.repo.PostRepository;
import com.example.demo.repo.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("john");
        testUser.setEmail("john@test.com");
        testUser.setRole(Role.PERSONAL);
        testUser.setFullName("John Doe");
        testUser.setBio("Hello");
        testUser.setPrivate(false);
    }

    // ===============================
    // 1️⃣ getMyProfile - Success
    // ===============================
    @Test
    void getMyProfile_ShouldReturnFullProfile() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(testUser));

        ProfileResponse response = profileService.getMyProfile("john");

        assertNotNull(response);
        assertEquals("john", response.username());
        assertFalse(response.limitedView());
    }

    // ===============================
    // 2️⃣ getMyProfile - User Not Found
    // ===============================
    @Test
    void getMyProfile_UserNotFound_ShouldThrowException() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.empty());

        assertThrows(CustomException.class,
                () -> profileService.getMyProfile("john"));
    }

    // ===============================
    // 3️⃣ getProfile - Public Account
    // ===============================
    @Test
    void getProfile_PublicAccount_ShouldReturnFullProfile() {

        testUser.setPrivate(false);

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(testUser));

        ProfileResponse response =
                profileService.getProfile("john", "alice");

        assertFalse(response.limitedView());
        assertEquals("Hello", response.bio());
    }

    // ===============================
    // 4️⃣ getProfile - Private (Not Owner)
    // ===============================
    @Test
    void getProfile_PrivateAccount_NotOwner_ShouldReturnLimitedView() {

        testUser.setPrivate(true);

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(testUser));

        ProfileResponse response =
                profileService.getProfile("john", "alice");

        assertTrue(response.limitedView());
        assertNull(response.bio());
    }

    // ===============================
    // 5️⃣ getProfile - Private (Owner)
    // ===============================
    @Test
    void getProfile_PrivateAccount_Owner_ShouldReturnFullProfile() {

        testUser.setPrivate(true);

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(testUser));

        ProfileResponse response =
                profileService.getProfile("john", "john");

        assertFalse(response.limitedView());
        assertEquals("Hello", response.bio());
    }

    // ===============================
    // 6️⃣ updateProfile - Basic Fields
    // ===============================
    @Test
    void updateProfile_ShouldUpdateBasicFields() {

    	UpdateProfileRequest request = new UpdateProfileRequest(
    	        null,               // username
    	        null,               // email
    	        null,               // role

    	        "New Name",         // fullName
    	        "New Bio",          // bio
    	        null,               // profilePicture
    	        null,               // bannerImage
    	        "New Location",     // location
    	        "www.site.com",     // website
    	        null,               // isPrivate

    	        null, null, null, null, null, null, null,   // CREATOR

    	        null, null, null, null, null, null, null,   // BUSINESS
    	        null
    	);

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(testUser));

        when(userRepository.save(any(User.class)))
                .thenReturn(testUser);

        ProfileResponse response =
                profileService.updateProfile("john", request);

        assertEquals("New Name", testUser.getFullName());
        assertEquals("New Bio", testUser.getBio());
    }

    // ===============================
    // 7️⃣ updateProfile - Username Exists
    // ===============================
    @Test
    void updateProfile_UsernameAlreadyExists_ShouldThrowException() {

    	UpdateProfileRequest request = new UpdateProfileRequest(
    	        "newjohn",          // username
    	        null,               // email
    	        null,               // role

    	        null, null, null, null, null, null, null,

    	        null, null, null, null, null, null, null,

    	        null, null, null, null, null, null, null,
    	        null
    	);

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(testUser));

        when(userRepository.existsByUsername("newjohn"))
                .thenReturn(true);

        assertThrows(CustomException.class,
                () -> profileService.updateProfile("john", request));
    }

    // ===============================
    // 8️⃣ updateProfile - Switch to CREATOR
    // ===============================
    @Test
    void updateProfile_SwitchToCreator_ShouldClearBusinessFields() {

        testUser.setRole(Role.BUSINESS);
        testUser.setBusinessName("Old Business");

        UpdateProfileRequest request = new UpdateProfileRequest(
                null,
                null,
                Role.CREATOR,       // role change

                null, null, null, null, null, null, null,

                "CreatorName",      // creatorName
                null, null, null, null, null, null,

                null, null, null, null, null, null, null,
                null
        );

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(testUser));

        profileService.updateProfile("john", request);

        assertEquals(Role.CREATOR, testUser.getRole());
        assertNull(testUser.getBusinessName());
    }

    // ===============================
    // 9️⃣ updateProfile - Switch to BUSINESS
    // ===============================
    @Test
    void updateProfile_SwitchToBusiness_ShouldClearCreatorFields() {

        testUser.setRole(Role.CREATOR);
        testUser.setCreatorName("Old Creator");

        UpdateProfileRequest request = new UpdateProfileRequest(
                null,
                null,
                Role.BUSINESS,      // role change

                null, null, null, null, null, null, null,

                null, null, null, null, null, null, null,

                "BusinessName",     // businessName
                null, null, null, null, null, null,
                null
        );

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(testUser));

        profileService.updateProfile("john", request);

        assertEquals(Role.BUSINESS, testUser.getRole());
        assertNull(testUser.getCreatorName());
    }

    // ===============================
    // 🔟 searchUsers
    // ===============================
    @Test
    void searchUsers_ShouldReturnMatchingUsers() {

        when(userRepository
                .findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(
                        "john", "john"))
                .thenReturn(List.of(testUser));

        List<ProfileResponse> result =
                profileService.searchUsers("john");

        assertEquals(1, result.size());
        assertEquals("john", result.get(0).username());
    }
}