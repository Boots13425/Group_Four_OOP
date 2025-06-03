package com.group4.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security Configuration - Configures Spring Security with JWT authentication
 * This implements the security requirements for all user scenarios including signup
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    
    /**
     * Password encoder bean for secure password hashing
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Authentication manager bean
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    /**
     * Security filter chain configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                // Public endpoints (no authentication required)
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers("/auth/login/student").permitAll()
                .requestMatchers("/auth/login/professor").permitAll()
                .requestMatchers("/auth/login/admin").permitAll()
                .requestMatchers("/auth/signup").permitAll()
                .requestMatchers("/auth/verify").permitAll()
                .requestMatchers("/auth/resend-verification").permitAll()
                .requestMatchers("/auth/check-availability").permitAll()
                .requestMatchers("/auth/validate").permitAll()
                
                // Student endpoints (students can only access their own data)
                .requestMatchers("/students/*/enroll").hasRole("STUDENT")
                .requestMatchers("/students/*/courses/*").hasRole("STUDENT")
                .requestMatchers("/students/*/grades").hasRole("STUDENT")
                .requestMatchers("/students/*/grades/**").hasRole("STUDENT")
                .requestMatchers("/students/courses/available").hasRole("STUDENT")
                
                // Professor endpoints
                .requestMatchers("/professors/**").hasRole("PROFESSOR")
                
                // Administrator endpoints
                .requestMatchers("/admin/**").hasRole("ADMINISTRATOR")
                
                // Any other request needs authentication
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
        // Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
