package com.example.demo.service;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.CustomException;
import com.example.demo.repo.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
                "prajwal",
                "prajwal@gmail.com",
                "Password123",
                Role.PERSONAL
        );

        loginRequest = new LoginRequest("prajwal", "Password123");

        user = new User(
                "prajwal",
                "prajwal@gmail.com",
                "encodedPassword",
                Role.PERSONAL
        );
    }

    // ✅ Test Registration Success
    @Test
    void register_shouldSaveUserSuccessfully() {

        when(userRepository.findByUsername("prajwal"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("Password123"))
                .thenReturn("encodedPassword");

        userService.register(registerRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }

    // ❌ Test Duplicate Username
    @Test
    void register_shouldThrowException_ifUsernameExists() {

        when(userRepository.findByUsername("prajwal"))
                .thenReturn(Optional.of(user));

        assertThrows(CustomException.class,
                () -> userService.register(registerRequest));
    }

    // ✅ Test Login Success
    @Test
    void login_shouldReturnToken_ifCredentialsValid() {

        when(userRepository.findByUsername("prajwal"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("Password123", "encodedPassword"))
                .thenReturn(true);

        when(jwtUtil.generateToken(user))
                .thenReturn("mocked-jwt-token");

        String token = userService.login(loginRequest);

        assertEquals("mocked-jwt-token", token);
    }

    // ❌ Test Login Invalid Password
    @Test
    void login_shouldThrowException_ifPasswordInvalid() {

        when(userRepository.findByUsername("prajwal"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("Password123", "encodedPassword"))
                .thenReturn(false);

        assertThrows(CustomException.class,
                () -> userService.login(loginRequest));
    }

    // ❌ Test Login User Not Found
    @Test
    void login_shouldThrowException_ifUserNotFound() {

        when(userRepository.findByUsername("prajwal"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("prajwal"))
                .thenReturn(Optional.empty());

        assertThrows(CustomException.class,
                () -> userService.login(loginRequest));
    }
}