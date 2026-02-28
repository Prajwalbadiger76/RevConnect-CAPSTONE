package com.example.demo.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            //  Disable CSRF for JWT
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                   session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )

            //  URL Authorization Rules
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/login",
                            "/register",
                            "/api/auth/**",
                            "/images/**",
                            "/css/**",
                            "/js/**"
                    ).permitAll()
                    
                    .requestMatchers("/creator/**").hasRole("CREATOR")
                    .requestMatchers("/business/**").hasRole("BUSINESS")
                    .requestMatchers("/personal/**").hasRole("PERSONAL")
                    
                    .anyRequest().authenticated()
            )

            //  Custom Exception Handling
            .exceptionHandling(ex -> ex

                    // 401 - Not Authenticated
                    .authenticationEntryPoint((request, response, authException) -> {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Unauthorized: Please login first");
                    })

                    // 403 - Access Denied (RBAC Failure)
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("Access Denied: You don't have permission");
                    })
            );

        //  Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}