package com.group4.demo.service;

import com.group4.demo.model.Course;
import com.group4.demo.model.Enrollment;
import com.group4.demo.model.Grade;
import com.group4.demo.model.Student;
import com.group4.demo.repository.CourseRepository;
import com.group4.demo.repository.EnrollmentRepository;
import com.group4.demo.repository.GradeRepository;
import com.group4.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Student Service - Handles all student-related business logic
 * This implements the student use cases from your sequence diagrams
 */
@Service
@Transactional
public class StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    @Autowired
    private GradeRepository gradeRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * COURSE REGISTRATION SCENARIO
     * This implements the complete course registration flow from your sequence diagram:
     * 1. View available courses
     * 2. Check prerequisites (if any)
     * 3. Validate enrollment eligibility
     * 4. Register for course
     */
    public EnrollmentResponse registerForCourse(Long studentId, Long courseId, String term) {
        // Step 1: Find student
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        
        // Step 2: Find course
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found"));
        
        // Step 3: Validate course is active
        if (!course.isActive()) {
            return new EnrollmentResponse(false, "Course is not currently active");
        }
        
        // Step 4: Check if course has available slots
        if (!course.hasAvailableSlots()) {
            return new EnrollmentResponse(false, "Course is full. No available slots.");
        }
        
        // Step 5: Check if student is already enrolled
        Optional<Enrollment> existingEnrollment = enrollmentRepository
            .findActiveEnrollment(studentId, courseId);
        if (existingEnrollment.isPresent()) {
            return new EnrollmentResponse(false, "Student is already enrolled in this course");
        }
        
        // Step 6: Check prerequisites (simplified - in real system would be more complex)
        if (!checkPrerequisites(student, course)) {
            return new EnrollmentResponse(false, "Prerequisites not met for this course");
        }
        
        // Step 7: Create enrollment
        Enrollment enrollment = new Enrollment(student, course, term);
        enrollmentRepository.save(enrollment);
        
        // Step 8: Update course enrollment count (handled by database trigger)
        course.updateEnrollmentCount();
        courseRepository.save(course);
        
        // Step 9: Send notification
        notificationService.sendEnrollmentNotification(student, course, "enrolled");
        
        return new EnrollmentResponse(true, "Successfully enrolled in " + course.getCourseName());
    }
    
    /**
     * VIEW GRADES SCENARIO
     * This implements the grade viewing flow with GPA calculation
     */
    public StudentGradesResponse viewCurrentGrades(Long studentId) {
        // Step 1: Find student
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        
        // Step 2: Get all grades for student
        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        
        // Step 3: Calculate current GPA
        student.calculateGPA();
        studentRepository.save(student);
        
        // Step 4: Get current enrollments
        List<Enrollment> currentEnrollments = enrollmentRepository
            .findActiveEnrollmentsByStudentId(studentId);
        
        return new StudentGradesResponse(
                (double)  student.getCurrentGPA(),
            student.getTotalCredits(),
            grades,
            currentEnrollments,
            "Grades retrieved successfully"
        );
    }
    
    /**
     * VIEW HISTORICAL GRADES SCENARIO
     * This implements the historical grade viewing with term organization
     */
    public HistoricalGradesResponse viewHistoricalGrades(Long studentId) {
        // Step 1: Find student
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        
        // Step 2: Get all terms for student
        List<String> terms = gradeRepository.findDistinctTermsByStudentId(studentId);
        
        // Step 3: Organize grades by term
        HistoricalGradesResponse response = new HistoricalGradesResponse();
        
        for (String term : terms) {
            List<Grade> termGrades = gradeRepository.findByStudentIdAndTerm(studentId, term);
            response.addTermGrades(term, termGrades);
        }
        
        response.setMessage("Historical grades retrieved successfully");
        return response;
    }
    
    /**
     * DROP COURSE SCENARIO
     * Allow student to drop from a course
     */
    public EnrollmentResponse dropCourse(Long studentId, Long courseId) {
        // Find active enrollment
        Optional<Enrollment> enrollmentOpt = enrollmentRepository
            .findActiveEnrollment(studentId, courseId);
        
        if (enrollmentOpt.isEmpty()) {
            return new EnrollmentResponse(false, "Student is not enrolled in this course");
        }
        
        Enrollment enrollment = enrollmentOpt.get();
        
        // Drop the course
        enrollment.dropCourse();
        enrollmentRepository.save(enrollment);
        
        // Update course enrollment count
        Course course = enrollment.getCourse();
        course.updateEnrollmentCount();
        courseRepository.save(course);
        
        // Send notification
        notificationService.sendEnrollmentNotification(
            enrollment.getStudent(), course, "dropped");
        
        return new EnrollmentResponse(true, "Successfully dropped from " + course.getCourseName());
    }
    
    /**
     * GET AVAILABLE COURSES
     * Get list of courses available for enrollment
     */
    public List<Course> getAvailableCoursesForEnrollment() {
        return courseRepository.findAvailableCoursesForEnrollment();
    }
    
    /**
     * Helper method to check prerequisites
     * In a real system, this would check against a prerequisites table
     */
    private boolean checkPrerequisites(Student student, Course course) {
        // Simplified prerequisite check
        // In reality, you'd have a prerequisites table and more complex logic
        return true; // For now, assume all prerequisites are met
    }
    
    /**
     * Response classes for service methods
     */
    public static class EnrollmentResponse {
        private boolean success;
        private String message;
        
        public EnrollmentResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
    
    public static class StudentGradesResponse {
        private Double currentGPA;
        private Integer totalCredits;
        private List<Grade> grades;
        private List<Enrollment> currentEnrollments;
        private String message;
        
        public StudentGradesResponse(Double currentGPA, Integer totalCredits,
                                   List<Grade> grades, List<Enrollment> currentEnrollments, 
                                   String message) {
            this.currentGPA = currentGPA;
            this.totalCredits = totalCredits;
            this.grades = grades;
            this.currentEnrollments = currentEnrollments;
            this.message = message;
        }
        
        // Getters
        public Double getCurrentGPA() { return currentGPA; }
        public Integer getTotalCredits() { return totalCredits; }
        public List<Grade> getGrades() { return grades; }
        public List<Enrollment> getCurrentEnrollments() { return currentEnrollments; }
        public String getMessage() { return message; }
    }
    
    public static class HistoricalGradesResponse {
        private java.util.Map<String, List<Grade>> gradesByTerm = new java.util.HashMap<>();
        private String message;
        
        public void addTermGrades(String term, List<Grade> grades) {
            gradesByTerm.put(term, grades);
        }
        
        public java.util.Map<String, List<Grade>> getGradesByTerm() { return gradesByTerm; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
