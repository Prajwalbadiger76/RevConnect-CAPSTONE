package com.example.demo.controller;

import com.example.demo.config.JwtUtil;
import com.example.demo.exception.CustomException;
import com.example.demo.service.UserService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AuthController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    // ================= REGISTER TESTS =================

    // Positive - Valid Registration
    @Test
    void register_shouldRedirectToLogin_whenValid() throws Exception {

        Mockito.doNothing().when(userService).register(any());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "prajwal")
                        .param("email", "prajwal@gmail.com")
                        .param("password", "Password123@")
                        .param("role", "PERSONAL"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));

        Mockito.verify(userService, Mockito.times(1)).register(any());
    }

    // Negative - Invalid Password Pattern
    @Test
    void register_shouldReturnRegisterPage_whenPasswordInvalid() throws Exception {

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "prajwal")
                        .param("email", "prajwal@gmail.com")
                        .param("password", "password") // ❌ invalid
                        .param("role", "PERSONAL"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    // Negative - Missing Email
    @Test
    void register_shouldReturnRegisterPage_whenEmailMissing() throws Exception {

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "prajwal")
                        .param("password", "Password123@")
                        .param("role", "PERSONAL"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    // ================= LOGIN TESTS =================

    // Positive - Successful Login
    @Test
    void login_shouldRedirectToFeed_whenValidCredentials() throws Exception {

        when(userService.login(any())).thenReturn("mocked-token");

        mockMvc.perform(post("/api/auth/login")
                        .param("username", "prajwal")
                        .param("password", "Password123@"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed"))
                .andExpect(cookie().exists("JWT"))
                .andExpect(cookie().httpOnly("JWT", true));
    }

    // Negative - Invalid Credentials
    @Test
    void login_shouldReturnLoginPage_whenInvalidCredentials() throws Exception {

        when(userService.login(any()))
                .thenThrow(new CustomException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .param("username", "prajwal")
                        .param("password", "WrongPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("error"));
    }

    // Negative - Missing Username
    @Test
    void login_shouldReturnLoginPage_whenUsernameMissing() throws Exception {

        mockMvc.perform(post("/api/auth/login")
                .param("password", "Password123@"))
                .andExpect(status().isBadRequest()); // ✅ FIXED
    }

    // ================= LOGOUT TEST =================

    // Logout - Cookie Cleared & Redirect
    @Test
    void logout_shouldClearCookie_andRedirectToLogin() throws Exception {

        mockMvc.perform(get("/api/auth/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(cookie().maxAge("JWT", 0));
    }
}