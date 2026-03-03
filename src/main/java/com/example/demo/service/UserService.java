package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.ProfileResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User; 

public interface UserService {

    void register(RegisterRequest request);

    String login(LoginRequest request);
    
    List<ProfileResponse> searchUsers(String keyword);
    
    User findByUsername(String username);
}
