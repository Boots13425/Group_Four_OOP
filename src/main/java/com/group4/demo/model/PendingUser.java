package com.group4.demo.model;

import com.group4.demo.service.UserRegistrationService;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * PendingUser class - Stores user registration data before verification
 * This handles the signup process with email/matricule verification
 */
@Entity
@Table(name = "pending_users")
public class PendingUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password; // Encrypted password
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String email;
    
    @Column(name = "identifier", nullable = false, unique = true)
    private String identifier; // Matricule or email used for registration
    
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserRegistrationService.UserType userType;
    
    @Column(name = "verification_code", nullable = false)
    private String verificationCode;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "is_verified")
    private boolean isVerified = false;
    
    // Constructors
    public PendingUser() {
        this.createdAt = LocalDateTime.now();
    }
    
    public PendingUser(String username, String password, String name, String email, 
                      String identifier, UserRegistrationService.UserType userType, String verificationCode) {
        this();
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.identifier = identifier;
        this.userType = userType;
        this.verificationCode = verificationCode;
    }
    
    // Business Methods
    
    /**
     * Check if verification code has expired (24 hours)
     */
    public boolean isVerificationExpired() {
        return createdAt.isBefore(LocalDateTime.now().minusHours(24));
    }
    
    /**
     * Verify the account
     */
    public void verify() {
        this.isVerified = true;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    
    public UserRegistrationService.UserType getUserType() { return userType; }
    public void setUserType(UserRegistrationService.UserType userType) { this.userType = userType; }
    
    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
}
