package com.group4.demo.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Student class - Represents students in the system
 * Adapted to match the provided Student.java structure
 */
@Entity
@DiscriminatorValue("STUDENT")
public class Student extends User {

    @Id
    @Column(name = "student_id", unique = true)
    private String studentID;
    
    @Column(name = "major")
    private String major;
    
    @Column(name = "enrollment_year")
    private int enrollmentYear;
    
    @Column(name = "current_gpa")
    private float currentGPA = 0.0f;
    
    @Column(name = "total_credits")
    private Integer totalCredits = 0;

    @Column(nullable = false)
    private String name;
    
    // One student can have many enrollments
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();
    
    // One student can have many grades
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Grade> grades = new ArrayList<>();

    // Constructors
    public Student() {
        super();
        setRole("STUDENT");
    }
    
    public Student(String userID, String username, String password,String name, String email,
                  String studentID, String major, int enrollmentYear) {
        super(userID, username, password, name ,email, "STUDENT");
        this.studentID = studentID;
        this.major = major;
        this.enrollmentYear = enrollmentYear;
    }

    public Student(String username, String password, String name, String email, String studentID) {
        super(null, username, password, name, email, "STUDENT");
        this.studentID = studentID;
    }


    /**
     * View grades method - displays student's current grades
     */
    public void viewGrades() {
        System.out.println("Grades for student: " + getUsername());
        for (Grade grade : grades) {
            System.out.println("Course: " + grade.getCourse().getCourseName() + 
                             ", Grade: " + grade.getLetterGrade() + 
                             ", Value: " + grade.getValue());
        }
        System.out.println("Current GPA: " + currentGPA);
    }
    
    /**
     * Export grades data method - exports student's grades to file
     */
    public void exportGradesData() {
        System.out.println("Exporting grades data for student: " + getUsername());
        // Implementation for exporting grades data
        // This could export to PDF, CSV, etc.
    }
    
    /**
     * View historical grades method - shows grades from previous terms
     */
    public void viewHistoricalGrades() {
        System.out.println("Historical grades for student: " + getUsername());
        // Group grades by semester/term and display
        for (Grade grade : grades) {
            System.out.println("Term: " + grade.getTerm() + 
                             ", Course: " + grade.getCourse().getCourseName() + 
                             ", Grade: " + grade.getLetterGrade());
        }
    }
    
    /**
     * Register course method - enrolls student in a course
     */
    public void registerCourse(Course course) {
        if (course.getCurrentEnrollment() < course.getMaxCapacity()) {
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(this);
            enrollment.setCourse(course);
            enrollment.setStatus(EnrollmentStatus.ACTIVE);
            enrollment.enrollStudent();
            
            enrollments.add(enrollment);
            course.addStudent(this);
            
            System.out.println("Student " + getUsername() + " registered for course: " + course.getCourseName());
        } else {
            System.out.println("Course " + course.getCourseName() + " is full. Cannot register.");
        }
    }
    
    /**
     * Calculate GPA based on current grades
     */
    public void calculateGPA() {
        if (grades.isEmpty()) {
            this.currentGPA = 0.0f;
            return;
        }
        
        float totalWeightedPoints = 0.0f;
        int totalCredits = 0;
        
        for (Grade grade : grades) {
            if (grade.getValue() >= 0 && grade.getCourse() != null) {
                float gradePoints = convertToGradePoints(grade.getValue());
                int courseCredits = grade.getCourse().getCredits();
                
                totalWeightedPoints += (gradePoints * courseCredits);
                totalCredits += courseCredits;
            }
        }
        
        this.currentGPA = totalCredits > 0 ? totalWeightedPoints / totalCredits : 0.0f;
        this.totalCredits = totalCredits;
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
    
    // Getters and Setters
    public String getStudentID() { return studentID; }
    public void setStudentID(String studentID) { this.studentID = studentID; }
    
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    
    public int getEnrollmentYear() { return enrollmentYear; }
    public void setEnrollmentYear(int enrollmentYear) { this.enrollmentYear = enrollmentYear; }
    
    public float getCurrentGPA() { return currentGPA; }
    public void setCurrentGPA(float currentGPA) { this.currentGPA = currentGPA; }
    
    public Integer getTotalCredits() { return totalCredits; }
    public void setTotalCredits(Integer totalCredits) { this.totalCredits = totalCredits; }
    
    public List<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; }
    
    public List<Grade> getGrades() { return grades; }
    public void setGrades(List<Grade> grades) { this.grades = grades; }
}
