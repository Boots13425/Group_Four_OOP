 package com.group4.demo.controller;

import com.group4.demo.service.AuthenticationService;
import com.group4.demo.service.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller - Handles login, authentication, and signup endpoints
 * This implements login scenarios with role-specific endpoints and user registration
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthenticationService authenticationService;
    
    @Autowired
    private UserRegistrationService userRegistrationService;
    
    /**
     * Student Login Endpoint
     * POST /auth/login/student
     */
    @PostMapping("/login/student")
    public ResponseEntity<?> studentLogin(@RequestBody LoginRequest loginRequest) {
        try {
            // Validate input
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Username is required"));
            }
            
            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Password is required"));
            }
            
            // Authenticate student
            AuthenticationService.LoginResponse response = authenticationService
                .authenticateStudent(loginRequest.getUsername(), loginRequest.getPassword());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Professor Login Endpoint
     * POST /auth/login/professor
     */
    @PostMapping("/login/professor")
    public ResponseEntity<?> professorLogin(@RequestBody LoginRequest loginRequest) {
        try {
            // Validate input
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Username is required"));
            }
            
            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Password is required"));
            }
            
            // Authenticate professor
            AuthenticationService.LoginResponse response = authenticationService
                .authenticateProfessor(loginRequest.getUsername(), loginRequest.getPassword());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Administrator Login Endpoint
     * POST /auth/login/admin
     */
    @PostMapping("/login/admin")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest loginRequest) {
        try {
            // Validate input
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Username is required"));
            }
            
            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Password is required"));
            }
            
            // Authenticate administrator
            AuthenticationService.LoginResponse response = authenticationService
                .authenticateAdmin(loginRequest.getUsername(), loginRequest.getPassword());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Generic Login Endpoint (maintained for backward compatibility)
     * POST /auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Validate input
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Username is required"));
            }
            
            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Password is required"));
            }
            
            // Authenticate user (any role)
            AuthenticationService.LoginResponse response = authenticationService
                .authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * USER SIGNUP ENDPOINT
     * POST /auth/signup
     * 
     * This allows users to register with their matricule/email and password
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        try {
            // Validate input
            if (signupRequest.getMatriculeOrEmail() == null || signupRequest.getMatriculeOrEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Matricule or Email is required"));
            }
            
            if (signupRequest.getPassword() == null || signupRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Password is required"));
            }
            
            if (signupRequest.getConfirmPassword() == null || signupRequest.getConfirmPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Password confirmation is required"));
            }
            
            if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Passwords do not match"));
            }
            
            if (signupRequest.getName() == null || signupRequest.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Full name is required"));
            }
            
            // Process signup
            UserRegistrationService.SignupResponse response = userRegistrationService.registerUser(signupRequest);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * CHECK MATRICULE/EMAIL AVAILABILITY
     * GET /auth/check-availability?identifier={matriculeOrEmail}
     */
    @GetMapping("/check-availability")
    public ResponseEntity<?> checkAvailability(@RequestParam String identifier) {
        try {
            boolean isAvailable = userRegistrationService.checkIdentifierAvailability(identifier);
            
            return ResponseEntity.ok(new AvailabilityResponse(isAvailable, 
                isAvailable ? "Identifier is available" : "Identifier is already taken"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Error checking availability: " + e.getMessage()));
        }
    }
    
    /**
     * VERIFY ACCOUNT ENDPOINT
     * POST /auth/verify
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestBody VerificationRequest verificationRequest) {
        try {
            UserRegistrationService.VerificationResponse response = userRegistrationService
                .verifyAccount(verificationRequest.getMatriculeOrEmail(), verificationRequest.getVerificationCode());
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * RESEND VERIFICATION CODE
     * POST /auth/resend-verification
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody ResendVerificationRequest request) {
        try {
            UserRegistrationService.ResendResponse response = userRegistrationService
                .resendVerificationCode(request.getMatriculeOrEmail());
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Validate Token Endpoint
     * GET /auth/validate
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Invalid authorization header"));
            }
            
            String token = authHeader.substring(7);
            var user = authenticationService.validateTokenAndGetUser(token);
            
            return ResponseEntity.ok(new ValidationResponse(true, "Token is valid", user.getRole().toString()));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Request classes for API endpoints
     */
    public static class LoginRequest {
        private String username;
        private String password;
        
        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class SignupRequest {
        private String matriculeOrEmail;
        private String password;
        private String confirmPassword;
        private String name;
        private String phoneNumber;
        private String userType; // "STUDENT" or "PROFESSOR" (optional, defaults to STUDENT)
        
        // Getters and setters
        public String getMatriculeOrEmail() { return matriculeOrEmail; }
        public void setMatriculeOrEmail(String matriculeOrEmail) { this.matriculeOrEmail = matriculeOrEmail; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        
        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
    }
    
    public static class VerificationRequest {
        private String matriculeOrEmail;
        private String verificationCode;
        
        public String getMatriculeOrEmail() { return matriculeOrEmail; }
        public void setMatriculeOrEmail(String matriculeOrEmail) { this.matriculeOrEmail = matriculeOrEmail; }
        
        public String getVerificationCode() { return verificationCode; }
        public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }
    }
    
    public static class ResendVerificationRequest {
        private String matriculeOrEmail;
        
        public String getMatriculeOrEmail() { return matriculeOrEmail; }
        public void setMatriculeOrEmail(String matriculeOrEmail) { this.matriculeOrEmail = matriculeOrEmail; }
    }
    
    public static class ErrorResponse {
        private String error;
        private long timestamp;
        
        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getError() { return error; }
        public long getTimestamp() { return timestamp; }
    }
    
    public static class ValidationResponse {
        private boolean valid;
        private String message;
        private String role;
        
        public ValidationResponse(boolean valid, String message, String role) {
            this.valid = valid;
            this.message = message;
            this.role = role;
        }
        
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
        public String getRole() { return role; }
    }
    
    public static class AvailabilityResponse {
        private boolean available;
        private String message;
        
        public AvailabilityResponse(boolean available, String message) {
            this.available = available;
            this.message = message;
        }
        
        public boolean isAvailable() { return available; }
        public String getMessage() { return message; }
    }
}
