package com.group4.demo.model;

/**
 * Enum for enrollment status
 * Defines the possible states of a student's enrollment in a course
 */
public enum EnrollmentStatus {
    ACTIVE("Active"),
    DROPPED("Dropped"),
    COMPLETED("Completed"),
    WITHDRAWN("Withdrawn");
    
    private final String displayName;
    
    EnrollmentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
