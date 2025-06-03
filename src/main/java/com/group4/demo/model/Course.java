package com.group4.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Course class - Represents courses offered by the university
 * Adapted to match the provided Course.java structure
 */
@Entity
@Table(name = "courses")
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "course_id", unique = true, nullable = false)
    private String courseID;
    
    @Column(name = "course_name", nullable = false)
    private String courseName;
    
    @Column(nullable = false)
    private int credits;
    
    @Column(nullable = false)
    private String semester;
    
    @Column(name = "max_capacity", nullable = false)
    private int maxCapacity;
    
    @Column(name = "current_enrollment")
    private int currentEnrollment = 0;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "is_active")
    private boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Many courses can be taught by one professor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    private Professor professor;
    
    // One course can have many enrollments
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();
    
    // One course can have many grades
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Grade> grades = new ArrayList<>();
    
    // Many courses can use one grade policy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_policy_id")
    private GradePolicy gradePolicy;
    
    // Constructors
    public Course() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Course(String courseID, String courseName, int credits, String semester, 
                  int maxCapacity, Professor professor) {
        this();
        this.courseID = courseID;
        this.courseName = courseName;
        this.credits = credits;
        this.semester = semester;
        this.maxCapacity = maxCapacity;
        this.professor = professor;
    }



    /**
     * Add student to course - handles student enrollment
     */


    public void addStudent(Student student) {
        if (currentEnrollment < maxCapacity) {
            // Check if student is already enrolled
            boolean alreadyEnrolled = enrollments.stream()
                .anyMatch(enrollment -> enrollment.getStudent().equals(student) && 
                         enrollment.getStatus() == EnrollmentStatus.ACTIVE);
            
            if (!alreadyEnrolled) {
                Enrollment enrollment = new Enrollment();
                enrollment.setStudent(student);
                enrollment.setCourse(this);
                enrollment.setStatus(EnrollmentStatus.ACTIVE);
                enrollment.setEnrollmentDate(LocalDateTime.now().toString());
                
                enrollments.add(enrollment);
                currentEnrollment++;
                
                System.out.println("Student " + student.getUsername() + 
                                 " added to course: " + courseName);
            } else {
                System.out.println("Student " + student.getUsername() + 
                                 " is already enrolled in course: " + courseName);
            }
        } else {
            System.out.println("Course " + courseName + " is at full capacity");
        }
    }
    
    /**
     * Remove student from course - handles student withdrawal
     */
    public void removeStudent(Student student) {
        boolean removed = enrollments.removeIf(enrollment -> 
            enrollment.getStudent().equals(student) && 
            enrollment.getStatus() == EnrollmentStatus.ACTIVE);
        
        if (removed) {
            currentEnrollment--;
            System.out.println("Student " + student.getUsername() + 
                             " removed from course: " + courseName);
        } else {
            System.out.println("Student " + student.getUsername() + 
                             " not found in course: " + courseName);
        }
    }
    
    /**
     * Update course information - allows modification of course details
     */
    public void updateCourseInfo(String newCourseName, int newCredits, String newSemester, 
                                int newMaxCapacity, String newDescription) {
        if (newCourseName != null && !newCourseName.isEmpty()) {
            this.courseName = newCourseName;
        }
        if (newCredits > 0) {
            this.credits = newCredits;
        }
        if (newSemester != null && !newSemester.isEmpty()) {
            this.semester = newSemester;
        }
        if (newMaxCapacity > 0) {
            // Only allow increasing capacity or decreasing if current enrollment allows
            if (newMaxCapacity >= currentEnrollment) {
                this.maxCapacity = newMaxCapacity;
            } else {
                System.out.println("Cannot reduce capacity below current enrollment: " + currentEnrollment);
            }
        }
        if (newDescription != null) {
            this.description = newDescription;
        }
        
        System.out.println("Course information updated for: " + courseName);
    }
    
    /**
     * Check if course has available slots for enrollment
     */
    public boolean hasAvailableSlots() {
        return currentEnrollment < maxCapacity;
    }
    
    /**
     * Get number of available slots
     */
    public int getAvailableSlots() {
        return maxCapacity - currentEnrollment;
    }
    
    /**
     * Update current enrollment count
     */
    public void updateEnrollmentCount() {
        this.currentEnrollment = (int) enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ACTIVE)
                .count();
    }
    
    /**
     * Get active students in this course
     */
    public List<Student> getActiveStudents() {
        return enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ACTIVE)
                .map(Enrollment::getStudent)
                .toList();
    }
    
    /**
     * Get course statistics
     */
    public void displayCourseStats() {
        System.out.println("=== Course Statistics ===");
        System.out.println("Course: " + courseName + " (" + courseID + ")");
        System.out.println("Credits: " + credits);
        System.out.println("Semester: " + semester);
        System.out.println("Enrollment: " + currentEnrollment + "/" + maxCapacity);
        System.out.println("Professor: " + (professor != null ? professor.getUsername() : "Not assigned"));
        System.out.println("Active: " + isActive);
    }
    
    // Getters and Setters

    public String getCourseCode() {
        return this.courseID;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCourseID() { return courseID; }
    public void setCourseID(String courseID) { this.courseID = courseID; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    public int getMaxCapacity() { return maxCapacity; }

    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity;
    }
    
    public int getCurrentEnrollment() { return currentEnrollment; }
    public void setCurrentEnrollment(int currentEnrollment) { this.currentEnrollment = currentEnrollment; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Professor getProfessor() { return professor; }
    public void setProfessor(Professor professor) { this.professor = professor; }
    
    public List<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; }
    
    public List<Grade> getGrades() { return grades; }
    public void setGrades(List<Grade> grades) { this.grades = grades; }
    
    public GradePolicy getGradePolicy() { return gradePolicy; }
    public void setGradePolicy(GradePolicy gradePolicy) { this.gradePolicy = gradePolicy; }
}
