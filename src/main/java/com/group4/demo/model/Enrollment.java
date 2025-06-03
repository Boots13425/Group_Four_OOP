package com.group4.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Enrollment class - Links students to courses
 * Adapted to match the provided Enrollment.java structure
 */
@Entity
@Table(name = "enrollments")
public class Enrollment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "enrollment_id", unique = true)
    private String enrollmentID;
    
    @Column(name = "enrollment_date")
    private String enrollmentDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;
    
    @Column(name = "drop_date")
    private LocalDateTime dropDate;
    
    @Column(name = "term")
    private String term;
    
    // Many enrollments belong to one student
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    // Many enrollments belong to one course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    // Constructors
    public Enrollment() {
        this.enrollmentID = generateEnrollmentID();
        this.enrollmentDate = LocalDateTime.now().toString();
        this.status = EnrollmentStatus.ACTIVE;
    }
    
    public Enrollment(Student student, Course course) {
        this();
        this.student = student;
        this.course = course;
    }
    
    public Enrollment(Student student, Course course, String term) {
        this(student, course);
        this.term = term;
    }
    
    /**
     * Enroll student in course - activates the enrollment
     */
    public void enrollStudent() {
        if (course.hasAvailableSlots()) {
            this.status = EnrollmentStatus.ACTIVE;
            this.enrollmentDate = LocalDateTime.now().toString();
            
            // Update course enrollment count
            course.setCurrentEnrollment(course.getCurrentEnrollment() + 1);
            
            System.out.println("Student " + student.getUsername() + 
                             " enrolled in course: " + course.getCourseName());
        } else {
            System.out.println("Cannot enroll - course " + course.getCourseName() + 
                             " is at full capacity");
        }
    }
    
    /**
     * Drop course - changes enrollment status to dropped
     */
    public void dropCourse() {
        if (this.status == EnrollmentStatus.ACTIVE) {
            this.status = EnrollmentStatus.DROPPED;
            this.dropDate = LocalDateTime.now();
            
            // Update course enrollment count
            course.setCurrentEnrollment(course.getCurrentEnrollment() - 1);
            
            System.out.println("Student " + student.getUsername() + 
                             " dropped from course: " + course.getCourseName());
        } else {
            System.out.println("Cannot drop - enrollment is not active");
        }
    }
    
    /**
     * Update enrollment status
     */
    public void updateStatus(EnrollmentStatus newStatus) {
        EnrollmentStatus oldStatus = this.status;
        this.status = newStatus;
        
        // Handle enrollment count changes
        if (oldStatus == EnrollmentStatus.ACTIVE && newStatus != EnrollmentStatus.ACTIVE) {
            // Student is leaving active enrollment
            course.setCurrentEnrollment(course.getCurrentEnrollment() - 1);
        } else if (oldStatus != EnrollmentStatus.ACTIVE && newStatus == EnrollmentStatus.ACTIVE) {
            // Student is becoming actively enrolled
            if (course.hasAvailableSlots()) {
                course.setCurrentEnrollment(course.getCurrentEnrollment() + 1);
            } else {
                // Revert status change if no capacity
                this.status = oldStatus;
                System.out.println("Cannot activate enrollment - course is at full capacity");
                return;
            }
        }
        
        // Set drop date if status changed to dropped or withdrawn
        if (newStatus == EnrollmentStatus.DROPPED || newStatus == EnrollmentStatus.WITHDRAWN) {
            this.dropDate = LocalDateTime.now();
        } else if (newStatus == EnrollmentStatus.ACTIVE) {
            this.dropDate = null; // Clear drop date if reactivating
        }
        
        System.out.println("Enrollment status updated from " + oldStatus + 
                         " to " + newStatus + " for student " + student.getUsername());
    }
    
    /**
     * Check if enrollment is currently active
     */
    public boolean isActive() {
        return status == EnrollmentStatus.ACTIVE;
    }
    
    /**
     * Get enrollment duration in days
     */
    public long getEnrollmentDurationDays() {
        LocalDateTime startDate = LocalDateTime.parse(enrollmentDate);
        LocalDateTime endDate = dropDate != null ? dropDate : LocalDateTime.now();
        return java.time.Duration.between(startDate, endDate).toDays();
    }
    
    /**
     * Generate unique enrollment ID
     */
    private String generateEnrollmentID() {
        return "ENR" + System.currentTimeMillis();
    }
    
    /**
     * Display enrollment information
     */
    public void displayEnrollmentInfo() {
        System.out.println("=== Enrollment Information ===");
        System.out.println("Enrollment ID: " + enrollmentID);
        System.out.println("Student: " + (student != null ? student.getUsername() : "Unknown"));
        System.out.println("Course: " + (course != null ? course.getCourseName() : "Unknown"));
        System.out.println("Status: " + status);
        System.out.println("Enrollment Date: " + enrollmentDate);
        System.out.println("Drop Date: " + (dropDate != null ? dropDate : "N/A"));
        System.out.println("Term: " + (term != null ? term : "N/A"));
        System.out.println("Duration: " + getEnrollmentDurationDays() + " days");
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEnrollmentID() { return enrollmentID; }
    public void setEnrollmentID(String enrollmentID) { this.enrollmentID = enrollmentID; }
    
    public String getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(String enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    
    public EnrollmentStatus getStatus() { return status; }
    public void setStatus(EnrollmentStatus status) { this.status = status; }
    
    public LocalDateTime getDropDate() { return dropDate; }
    public void setDropDate(LocalDateTime dropDate) { this.dropDate = dropDate; }
    
    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
    
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
}
