package com.example.demo.service;

import com.example.demo.dto.ProfileResponse;
import com.example.demo.dto.UpdateProfileRequest;

import java.util.List;

public interface ProfileService {

    // ================= GET MY PROFILE =================
    ProfileResponse getMyProfile(String username);

    // ================= GET PUBLIC PROFILE (Soft Privacy) =================
    ProfileResponse getProfile(String username, String viewerUsername);

    // ================= UPDATE PROFILE =================
    ProfileResponse updateProfile(String username, UpdateProfileRequest request);

    // ================= SEARCH USERS =================
    List<ProfileResponse> searchUsers(String keyword);
}