package com.example.demo.controller;

import com.example.demo.config.JwtUtil;
import com.example.demo.service.UserService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = AuthController.class,
excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    
    @MockBean
    private JwtUtil jwtUtil; 

    @Test
    void register_shouldRedirectToLogin() throws Exception {

        mockMvc.perform(post("/api/auth/register")
                .param("username", "prajwal")
                .param("email", "prajwal@gmail.com")
                .param("password", "Password123")
                .param("role", "PERSONAL"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void login_shouldRedirectToFeed_ifSuccess() throws Exception {

        when(userService.login(any()))
                .thenReturn("mocked-token");

        mockMvc.perform(post("/api/auth/login")
                .param("username", "prajwal")
                .param("password", "Password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/feed"));
    }
}