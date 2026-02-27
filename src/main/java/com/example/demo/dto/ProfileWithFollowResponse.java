package com.example.demo.dto;

import com.example.demo.entity.Role;

public record ProfileWithFollowResponse(

		
        String username,
        String email,
        Role role,
        String fullName,
        String bio,
        String profilePicture,
        String bannerImage,
        String location,
        String website,
        Boolean isPrivate,

        // 🔥 Add missing fields
        String creatorName,
        String industry,
        String category,
        String businessName,
        String servicesOffered,
        String skills,

        boolean isFollowing,
        long followerCount,
        long followingCount,
        boolean isOwn
) {}