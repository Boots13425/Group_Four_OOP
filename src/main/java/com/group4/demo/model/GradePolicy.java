package com.group4.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GradePolicy class - Defines grading rules and scales
 * Adapted to match the provided GradePolicy.java structure
 */
@Entity
@Table(name = "grade_policies")
public class GradePolicy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "policy_id", unique = true, nullable = false)
    private String policyID;
    
    @Column(name = "policy_name", nullable = false)
    private String policyName;
    
    // Store grading scale as JSON string in database
    @Column(name = "grading_scale", columnDefinition = "TEXT")
    private String gradingScaleJson;
    
    // Transient field for in-memory grading scale map
    @Transient
    private Map<String, Object> gradingScale = new HashMap<>();
    
    @Column(name = "passing_grade", nullable = false)
    private float passingGrade = 40.0f;
    
    @Column(name = "late_submission_policy", columnDefinition = "TEXT")
    private String lateSubmissionPolicy;
    
    @Column(name = "late_penalty")
    private Float latePenalty;
    
    @Column(name = "max_score")
    private Integer maxScore = 100;
    
    @Column(name = "is_active")
    private boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    // One grade policy can be used by many courses
    @OneToMany(mappedBy = "gradePolicy", fetch = FetchType.LAZY)
    private List<Course> courses = new ArrayList<>();
    
    // Constructors
    public GradePolicy() {
        this.createdAt = LocalDateTime.now();
        this.policyID = generatePolicyID();
        initializeDefaultGradingScale();
    }
    
    public GradePolicy(String policyName, float passingGrade, String lateSubmissionPolicy, String createdBy) {
        this();
        this.policyName = policyName;
        this.passingGrade = passingGrade;
        this.lateSubmissionPolicy = lateSubmissionPolicy;
        this.createdBy = createdBy;
    }
    
    /**
     * Update policy settings
     */
    public void updatePolicy() {
        updatePolicy(this.policyName, this.passingGrade, this.lateSubmissionPolicy);
    }
    
    public void updatePolicy(String newPolicyName, float newPassingGrade, String newLateSubmissionPolicy) {
        if (newPolicyName != null && !newPolicyName.isEmpty()) {
            this.policyName = newPolicyName;
        }
        
        if (newPassingGrade >= 0 && newPassingGrade <= 100) {
            this.passingGrade = newPassingGrade;
        }
        
        if (newLateSubmissionPolicy != null) {
            this.lateSubmissionPolicy = newLateSubmissionPolicy;
        }
        
        System.out.println("Grade policy updated: " + policyName);
    }
    
    /**
     * Apply policy to a grade value
     */
    public void applyPolicy() {
        applyPolicy(0.0f); // Default application
    }
    
    public float applyPolicy(float gradeValue) {
        // Apply late penalty if applicable
        if (latePenalty != null && latePenalty > 0) {
            float penaltyAmount = gradeValue * (latePenalty / 100.0f);
            gradeValue = Math.max(0.0f, gradeValue - penaltyAmount);
        }
        
        // Ensure grade is within valid range
        gradeValue = Math.max(0.0f, Math.min(maxScore.floatValue(), gradeValue));
        
        System.out.println("Policy applied. Final grade: " + gradeValue);
        return gradeValue;
    }
    
    /**
     * Validate grade against policy rules
     */
    public void validateGrade() {
        validateGrade(0.0f); // Default validation
    }
    
    public boolean validateGrade(float gradeValue) {
        // Check if grade is within valid range
        if (gradeValue < 0 || gradeValue > maxScore) {
            System.out.println("Invalid grade: " + gradeValue + 
                             ". Must be between 0 and " + maxScore);
            return false;
        }
        
        // Check if grade meets passing criteria
        boolean isPassing = gradeValue >= passingGrade;
        
        System.out.println("Grade validation: " + gradeValue + 
                         " - " + (isPassing ? "PASSING" : "FAILING"));
        
        return true; // Grade format is valid (regardless of pass/fail)
    }
    
    /**
     * Check if a grade is passing
     */
    public boolean isPassing(float gradeValue) {
        return gradeValue >= passingGrade;
    }
    
    /**
     * Get letter grade based on numerical value
     */
    public String getLetterGrade(float gradeValue) {
        if (gradeValue >= 80) return "A";
        else if (gradeValue >= 70) return "B+";
        else if (gradeValue >= 60) return "B";
        else if (gradeValue >= 55) return "C+";
        else if (gradeValue >= 50) return "C";
        else if (gradeValue >= 45) return "D+";
        else if (gradeValue >= 40) return "D";
        else return "F";
    }
    
    /**
     * Get grade points based on numerical value
     */
    public float getGradePoints(float gradeValue) {
        if (gradeValue >= 80) return 4.0f;
        else if (gradeValue >= 70) return 3.5f;
        else if (gradeValue >= 60) return 3.0f;
        else if (gradeValue >= 55) return 2.5f;
        else if (gradeValue >= 50) return 2.0f;
        else if (gradeValue >= 45) return 1.5f;
        else if (gradeValue >= 40) return 1.0f;
        else return 0.0f;
    }
    
    /**
     * Apply late penalty to a score
     */
    public float applyLatePenalty(float originalScore) {
        if (latePenalty == null || latePenalty == 0.0f) {
            return originalScore;
        }
        
        float penaltyAmount = originalScore * (latePenalty / 100.0f);
        float finalScore = originalScore - penaltyAmount;
        
        return Math.max(0.0f, finalScore);
    }
    
    /**
     * Initialize default grading scale
     */
    private void initializeDefaultGradingScale() {
        gradingScale.put("A", Map.of("min", 80, "max", 100, "points", 4.0f));
        gradingScale.put("B+", Map.of("min", 70, "max", 79, "points", 3.5f));
        gradingScale.put("B", Map.of("min", 60, "max", 69, "points", 3.0f));
        gradingScale.put("C+", Map.of("min", 55, "max", 59, "points", 2.5f));
        gradingScale.put("C", Map.of("min", 50, "max", 54, "points", 2.0f));
        gradingScale.put("D+", Map.of("min", 45, "max", 49, "points", 1.5f));
        gradingScale.put("D", Map.of("min", 40, "max", 44, "points", 1.0f));
        gradingScale.put("F", Map.of("min", 0, "max", 39, "points", 0.0f));
    }
    
    /**
     * Generate unique policy ID
     */
    private String generatePolicyID() {
        return "POL" + System.currentTimeMillis();
    }
    
    /**
     * Get default grade scale as formatted string
     */
    public static String getDefaultGradeScale() {
        return "80-100:A:4.0:EXCELLENT," +
               "70-79:B+:3.5:VERY GOOD," +
               "60-69:B:3.0:GOOD," +
               "55-59:C+:2.5:FAIR," +
               "50-54:C:2.0:AVERAGE," +
               "45-49:D+:1.5:BELOW AVERAGE," +
               "40-44:D:1.0:POOR," +
               "0-39:F:0:FAIL";
    }
    
    /**
     * Display policy information
     */
    public void displayPolicyInfo() {
        System.out.println("=== Grade Policy Information ===");
        System.out.println("Policy ID: " + policyID);
        System.out.println("Policy Name: " + policyName);
        System.out.println("Passing Grade: " + passingGrade);
        System.out.println("Max Score: " + maxScore);
        System.out.println("Late Penalty: " + (latePenalty != null ? latePenalty + "%" : "None"));
        System.out.println("Late Submission Policy: " + lateSubmissionPolicy);
        System.out.println("Active: " + isActive);
        System.out.println("Created By: " + createdBy);
        System.out.println("Created At: " + createdAt);
        System.out.println("Courses Using Policy: " + courses.size());
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPolicyID() { return policyID; }
    public void setPolicyID(String policyID) { this.policyID = policyID; }
    
    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }
    
    public Map<String, Object> getGradingScale() { return gradingScale; }
    public void setGradingScale(Map<String, Object> gradingScale) { this.gradingScale = gradingScale;}
    public String getGradingScaleJson() { return gradingScaleJson; }
    public void setGradingScaleJson(String gradingScaleJson) { this.gradingScaleJson = gradingScaleJson; }
    
    public float getPassingGrade() { return passingGrade; }
    public void setPassingGrade(float passingGrade) { this.passingGrade = passingGrade; }
    
    public String getLateSubmissionPolicy() { return lateSubmissionPolicy; }
    public void setLateSubmissionPolicy(String lateSubmissionPolicy) { this.lateSubmissionPolicy = lateSubmissionPolicy; }
    
    public Float getLatePenalty() { return latePenalty; }
    public void setLatePenalty(Float latePenalty) { this.latePenalty = latePenalty; }
    
    public Integer getMaxScore() { return maxScore; }
    public void setMaxScore(Integer maxScore) { this.maxScore = maxScore; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
}
