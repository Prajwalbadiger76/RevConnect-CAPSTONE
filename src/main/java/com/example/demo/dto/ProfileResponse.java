package com.example.demo.dto;

import com.example.demo.entity.Role;

public record ProfileResponse(
        String username,
        String email,
        Role role,
        String fullName,
        String bio,
        String profilePicture,
        String bannerImage,
        String location,
        String website,
        boolean isPrivate,

        // PROFILE MG PART - BUSINESS FIELDS
        String category,
        String contactInfo,
        String businessAddress,
        String businessHours,
        int completionPercentage,
        long postCount,
        long followerCount,
        long followingCount,
        boolean limitedView
) {}