package com.example.demo.dto;

import com.example.demo.entity.Role;
import jakarta.validation.constraints.*;

public record RegisterRequest(

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$",
            message = "Password must contain uppercase, lowercase, number and special character"
        )
        String password,

        @NotNull(message = "Role is required")
        Role role
) {}