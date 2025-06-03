package com.group4.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Grade class - Stores individual grade information for students
 * Adapted to match the provided Grade.java structure
 */
@Entity
@Table(name = "grades")
public class Grade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "grade_id", unique = true)
    private String gradeID;
    
    @Column(name = "value", nullable = false)
    private float value; // Numerical score (0-100)
    
    @Column(name = "letter_grade")
    private String letterGrade; // A, B+, B, C+, C, D+, D, F
    
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    
    @Column(name = "is_finalized")
    private boolean isFinalized = false;
    
    @Column(name = "term")
    private String term;
    
    @Column(name = "grade_points")
    private Float gradePoints;
    
    @Column(name = "graded_by")
    private String gradedBy;
    
    // Many grades belong to one student
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    // Many grades belong to one course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    // Constructors
    public Grade() {
        this.timestamp = LocalDateTime.now();
        this.gradeID = generateGradeID();
    }
    
    public Grade(Student student, Course course, float value, String term, String gradedBy) {
        this();
        this.student = student;
        this.course = course;
        this.value = value;
        this.term = term;
        this.gradedBy = gradedBy;
        calculateLetterGrade();
    }
    
    /**
     * Calculate GPA contribution of this grade
     */
    public void calculateGPA() {
        if (course != null) {
            this.gradePoints = convertToGradePoints(this.value);
            System.out.println("GPA calculated for grade: " + letterGrade + 
                             " (Points: " + gradePoints + ")");
        }
    }
    
    /**
     * Update grade value and recalculate letter grade
     */
    public void updateGrade() {
        if (!isFinalized) {
            calculateLetterGrade();
            this.timestamp = LocalDateTime.now();
            System.out.println("Grade updated to: " + letterGrade + " (" + value + ")");
        } else {
            System.out.println("Cannot update finalized grade");
        }
    }
    
    /**
     * Finalize grade - prevents further modifications
     */
    public void finalizeGrade() {
        this.isFinalized = true;
        this.timestamp = LocalDateTime.now();
        System.out.println("Grade finalized: " + letterGrade + " for student " + 
                         (student != null ? student.getUsername() : "Unknown"));
    }
    
    /**
     * Calculate letter grade and grade points based on numerical value
     */
    public void calculateLetterGrade() {
        if (value >= 80) {
            this.letterGrade = "A";
            this.gradePoints = 4.0f;
        } else if (value >= 70) {
            this.letterGrade = "B+";
            this.gradePoints = 3.5f;
        } else if (value >= 60) {
            this.letterGrade = "B";
            this.gradePoints = 3.0f;
        } else if (value >= 55) {
            this.letterGrade = "C+";
            this.gradePoints = 2.5f;
        } else if (value >= 50) {
            this.letterGrade = "C";
            this.gradePoints = 2.0f;
        } else if (value >= 45) {
            this.letterGrade = "D+";
            this.gradePoints = 1.5f;
        } else if (value >= 40) {
            this.letterGrade = "D";
            this.gradePoints = 1.0f;
        } else {
            this.letterGrade = "F";
            this.gradePoints = 0.0f;
        }
    }
    
    /**
     * Convert numerical grade to grade points
     */
    private float convertToGradePoints(float value) {
        if (value >= 80) return 4.0f;
        else if (value >= 70) return 3.5f;
        else if (value >= 60) return 3.0f;
        else if (value >= 55) return 2.5f;
        else if (value >= 50) return 2.0f;
        else if (value >= 45) return 1.5f;
        else if (value >= 40) return 1.0f;
        else return 0.0f;
    }
    
    /**
     * Get evaluation based on grade points
     */
    public String getEvaluation() {
        if (gradePoints == null) return "FAIL";
        
        if (gradePoints >= 4.0f) return "EXCELLENT";
        else if (gradePoints >= 3.5f) return "VERY GOOD";
        else if (gradePoints >= 3.0f) return "GOOD";
        else if (gradePoints >= 2.5f) return "FAIR";
        else if (gradePoints >= 2.0f) return "AVERAGE";
        else if (gradePoints >= 1.5f) return "BELOW AVERAGE";
        else if (gradePoints >= 1.0f) return "POOR";
        else return "FAIL";
    }
    
    /**
     * Generate unique grade ID
     */
    private String generateGradeID() {
        return "GRD" + System.currentTimeMillis();
    }
    
    /**
     * Display grade information
     */
    public void displayGradeInfo() {
        System.out.println("=== Grade Information ===");
        System.out.println("Grade ID: " + gradeID);
        System.out.println("Student: " + (student != null ? student.getUsername() : "Unknown"));
        System.out.println("Course: " + (course != null ? course.getCourseName() : "Unknown"));
        System.out.println("Value: " + value);
        System.out.println("Letter Grade: " + letterGrade);
        System.out.println("Grade Points: " + gradePoints);
        System.out.println("Evaluation: " + getEvaluation());
        System.out.println("Finalized: " + isFinalized);
        System.out.println("Timestamp: " + timestamp);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getGradeID() { return gradeID; }
    public void setGradeID(String gradeID) { this.gradeID = gradeID; }
    
    public float getValue() { return value; }


    public void setValue(float value) { 
        this.value = value;
        calculateLetterGrade();
    }
    
    public String getLetterGrade() { return letterGrade; }
    public void setLetterGrade(String letterGrade) { this.letterGrade = letterGrade; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public boolean isFinalized() { return isFinalized; }
    public void setFinalized(boolean finalized) { isFinalized = finalized; }
    
    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
    
    public Float getGradePoints() { return gradePoints; }
    public void setGradePoints(Float gradePoints) { this.gradePoints = gradePoints; }
    
    public String getGradedBy() { return gradedBy; }
    public void setGradedBy(String gradedBy) { this.gradedBy = gradedBy; }
    
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
}
