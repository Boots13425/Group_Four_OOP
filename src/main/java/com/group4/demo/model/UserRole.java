package com.group4.demo.model;

/**
 * Enum defining the different types of users in our system
 * Based on the actors identified in your use case diagram
 */


public enum UserRole {
    STUDENT("Student"),
    PROFESSOR("Professor"), 
    ADMINISTRATOR("Administrator");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }


    public String getDisplayName() {
        return displayName;
    }
}
