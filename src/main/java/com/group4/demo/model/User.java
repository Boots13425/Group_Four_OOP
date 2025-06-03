package com.group4.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;


/**
 * Abstract User class - Base class for all users in the system
 * Adapted to match the provided User.java structure
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public abstract class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", unique = true)
    private String userID;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String role;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(nullable = false)
    private String name;


    // Constructors
    public User() {
        this.createdAt = LocalDateTime.now();
    }
    
    public User(String userID, String username, String password,String name ,String email, String role) {
        this();
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = role;
    }
    
    /**
     * Login method - validates user credentials
     */
    public boolean login(String inputPassword) {
        // In real implementation, this would use password encoder
        return this.password.equals(inputPassword) && this.isActive;
    }
    
    /**
     * Logout method - handles user logout
     */
    public void logout() {
        // Implementation for logout logic
        System.out.println("User " + username + " logged out successfully");
    }
    
    /**
     * Update profile method - allows user to update their profile
     */
    public void updateProfile(String newEmail, String newPassword) {
        if (newEmail != null && !newEmail.isEmpty()) {
            this.email = newEmail;
        }
        if (newPassword != null && !newPassword.isEmpty()) {
            this.password = newPassword;
        }
        System.out.println("Profile updated for user: " + username);
    }
    
    /**
     * Generic operation method - can be overridden by subclasses
     */
    public void Operation1() {
        System.out.println("Generic operation performed by user: " + username);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
