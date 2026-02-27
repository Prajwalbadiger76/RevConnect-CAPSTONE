package com.example.demo.dto;

import com.example.demo.entity.Role;

public record UpdateProfileRequest(

        // BASIC
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

        // CREATOR
        String creatorName,
        String industry,
        String instagramLink,
        String youtubeLink,
        String twitterLink,
        String portfolioLink,
        String skills,

        // BUSINESS
        String businessName,
        String category,
        String contactInfo,
        String businessAddress,
        String businessHours,
        String businessDescription,
        String servicesOffered,
        String mapLocationLink

) {}