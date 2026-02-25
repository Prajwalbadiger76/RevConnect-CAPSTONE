package com.example.demo.service;

public class ProfileService {

<<<<<<< Updated upstream
}
=======
import java.util.List;

public interface ProfileService {

    ProfileResponse getMyProfile(String username);

    ProfileResponse getProfile(String username);

    ProfileResponse updateProfile(String username, UpdateProfileRequest request);

    List<ProfileResponse> searchUsers(String keyword);
    
    ProfileResponse getProfileWithFollowInfo(String currentUsername, String targetUsername);
    
}
>>>>>>> Stashed changes
