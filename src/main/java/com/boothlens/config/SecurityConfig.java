package com.boothlens.config;

//import JwtAuthenticationFilter(check if user is logged in and with permissions)
import com.boothlens.filter.JwtAuthenticationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Spring Security Configuration for Booth Lens Backend Application
 * This configuration handles authentication, authorization, and CORS settings
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Bean for password encoding using BCrypt algorithm
     * BCrypt is a strong hashing function designed for password hashing
     * 
     * @return BCryptPasswordEncoder instance for password hashing
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain for HTTP requests
     * Defines which endpoints are public and which require authentication
     * 
     * @param http HttpSecurity object to configure security settings
     * @return SecurityFilterChain configured security filter chain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable Cross-Origin
            //.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .cors(cors -> cors.disable())

            // Disable Cross-Site Request Forgery (CSRF) protection for REST API endpoints
            // 2. 關閉 CSRF (JWT 無狀態 API 不需要)
            .csrf(csrf -> csrf.disable())
            
            // Add JWT authentication filter (right before all the authentication filters)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            
            // Configure authorization rules for different endpoints
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")
                .requestMatchers("/api/superadmin/**").hasRole("SUPERADMIN")
                .requestMatchers("/api/profile/**").authenticated()
                .requestMatchers("/api/v1/booth/**").authenticated()
                .requestMatchers("/api/v1/user/**").authenticated()
                // All other requests are public
                .anyRequest().permitAll()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
} 