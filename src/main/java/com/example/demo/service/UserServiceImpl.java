package com.example.demo.service;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.exception.CustomException;
import com.example.demo.repo.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ================= REGISTER =================
    @Override
    public void register(RegisterRequest request) {

        // Check username
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new CustomException("Username already exists");
        }

        // Check email
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new CustomException("Email already exists");
        }

        User user = new User(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.role()
        );

        // VERY IMPORTANT: make sure image is null initially
        user.setProfilePicture(null);

        userRepository.save(user);
    }

    // ================= LOGIN =================
    @Override
    public String login(LoginRequest request) {

        User user = userRepository.findByUsername(request.username())
                .or(() -> userRepository.findByEmail(request.username()))
                .orElseThrow(() -> new CustomException("Invalid username/email"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException("Invalid password");
        }

        return jwtUtil.generateToken(user);
    }
}