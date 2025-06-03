package com.group4.demo.service;

import com.group4.demo.model.*;
import com.group4.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Professor Service - Handles all professor-related business logic
 * This implements the professor use cases from your sequence diagrams
 */
@Service
@Transactional
public class ProfessorService {
    
    @Autowired
    private ProfessorRepository professorRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private GradeRepository gradeRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * PROFESSOR LOGIN SCENARIO
     * Get professor dashboard with assigned courses
     */
    public ProfessorDashboardResponse getProfessorDashboard(Long professorId) {
        // Step 1: Find professor
        Professor professor = professorRepository.findById(professorId)
            .orElseThrow(() -> new RuntimeException("Professor not found"));
        
        // Step 2: Get assigned courses
        List<Course> assignedCourses = courseRepository.findByProfessorId(professorId);
        
        // Step 3: Calculate statistics
        int totalStudents = 0;
        int totalCourses = assignedCourses.size();
        
        for (Course course : assignedCourses) {
            totalStudents += course.getCurrentEnrollment();
        }
        
        return new ProfessorDashboardResponse(
            professor.getName(),
            professor.getDepartment(),
            assignedCourses,
            totalCourses,
            totalStudents,
            "Dashboard loaded successfully"
        );
    }
    
    /**
     * VIEW STUDENT GRADES SCENARIO
     * This implements the complete grade viewing flow for professors
     */
    public CourseGradesResponse viewStudentGrades(Long professorId, Long courseId) {
        // Step 1: Verify professor teaches this course
        Professor professor = professorRepository.findById(professorId)
            .orElseThrow(() -> new RuntimeException("Professor not found"));
        
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found"));
        
        if (!course.getProfessor().getId().equals(professorId)) {
            throw new RuntimeException("Professor is not assigned to this course");
        }
        
        // Step 2: Get enrolled students
        List<Enrollment> enrollments = enrollmentRepository.findActiveEnrollmentsByCourseId(courseId);
        
        // Step 3: Get grades for the course
        List<Grade> grades = gradeRepository.findByCourseId(courseId);
        
        // Step 4: Calculate course statistics
        Double averageGPA = gradeRepository.calculateAverageGPAForCourse(courseId);
        
        return new CourseGradesResponse(
            course,
            enrollments,
            grades,
            averageGPA != null ? averageGPA : 0.0,
            "Course grades retrieved successfully"
        );
    }
    
    /**
     * UPLOAD STUDENT MARKS SCENARIO
     * This implements the complete grade upload flow with validation
     */
    public GradeUploadResponse uploadStudentMarks(Long professorId, Long courseId, 
                                                 List<GradeUploadRequest> gradeRequests, String term) {
        // Step 1: Verify professor teaches this course
        Professor professor = professorRepository.findById(professorId)
            .orElseThrow(() -> new RuntimeException("Professor not found"));
        
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found"));
        
        if (!course.getProfessor().getId().equals(professorId)) {
            throw new RuntimeException("Professor is not assigned to this course");
        }
        
        // Step 2: Validate all grade data before processing
        for (GradeUploadRequest request : gradeRequests) {
            if (!validateGradeData(request)) {
                return new GradeUploadResponse(false, 
                    "Invalid grade data for student ID: " + request.getStudentId());
            }
        }
        
        // Step 3: Process each grade upload
        int successCount = 0;
        int errorCount = 0;
        StringBuilder errorMessages = new StringBuilder();
        
        for (GradeUploadRequest request : gradeRequests) {
            try {
                // Check if student is enrolled in the course
                Optional<Enrollment> enrollmentOpt = enrollmentRepository
                    .findActiveEnrollment(request.getStudentId(), courseId);
                
                if (enrollmentOpt.isEmpty()) {
                    errorCount++;
                    errorMessages.append("Student ID ").append(request.getStudentId())
                        .append(" is not enrolled in this course. ");
                    continue;
                }
                
                // Check if grade already exists
                Optional<Grade> existingGrade = gradeRepository
                    .findByStudentIdAndCourseIdAndTerm(request.getStudentId(), courseId, term);
                
                if (existingGrade.isPresent()) {
                    errorCount++;
                    errorMessages.append("Grade already exists for student ID ")
                        .append(request.getStudentId()).append(". Use update instead. ");
                    continue;
                }
                
                // Create new grade
                Student student = studentRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
                
                Grade grade = new Grade(student, course, request.getValue().floatValue(), term, professor.getName());
                gradeRepository.save(grade);
                
                // Update student GPA
                student.calculateGPA();
                studentRepository.save(student);
                
                // Send notification to student
                notificationService.sendGradeNotification(student, grade);
                
                successCount++;
                
            } catch (Exception e) {
                errorCount++;
                errorMessages.append("Error processing student ID ")
                    .append(request.getStudentId()).append(": ")
                    .append(e.getMessage()).append(". ");
            }
        }
        
        String message = String.format("Upload completed. Success: %d, Errors: %d", 
            successCount, errorCount);
        if (errorCount > 0) {
            message += ". Errors: " + errorMessages.toString();
        }
        
        return new GradeUploadResponse(errorCount == 0, message);
    }
    
    /**
     * UPDATE STUDENT MARKS SCENARIO
     * This implements the grade update flow with validation and logging
     */
    public GradeUpdateResponse updateStudentMark(Long professorId, Long gradeId, Double newScore) {
        // Step 1: Find the grade
        Grade grade = gradeRepository.findById(gradeId)
            .orElseThrow(() -> new RuntimeException("Grade not found"));
        
        // Step 2: Verify professor can update this grade
        if (!grade.getCourse().getProfessor().getId().equals(professorId)) {
            throw new RuntimeException("Professor is not authorized to update this grade");
        }
        
        // Step 3: Validate new score
        if (newScore < 0 || newScore > 100) {
            return new GradeUpdateResponse(false, "Score must be between 0 and 100");
        }
        
        // Step 4: Store old values for logging
     double oldScore = grade.getValue();
        String oldLetterGrade = grade.getLetterGrade();
        
        // Step 5: Update the grade

        grade.getValue(); //to come back and resolve it
        gradeRepository.save(grade);
        
        // Step 6: Recalculate student GPA
        Student student = grade.getStudent();
        student.calculateGPA();
        studentRepository.save(student);
        
        // Step 7: Send notification to student
        notificationService.sendGradeNotification(student, grade);
        
        // Step 8: Log the change (in a real system, you'd have an audit table)
        String logMessage = String.format(
            "Grade updated for student %s in course %s. Old: %.1f (%s), New: %.1f (%s)",
            student.getName(), grade.getCourse().getCourseName(),
            oldScore, oldLetterGrade, newScore, grade.getLetterGrade()
        );
        
        return new GradeUpdateResponse(true, "Grade updated successfully. " + logMessage);
    }
    
    /**
     * GET ENROLLED STUDENTS FOR A COURSE
     * This supports the grade management scenarios
     */
    public List<Student> getEnrolledStudents(Long professorId, Long courseId) {
        // Verify professor teaches this course
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found"));
        
        if (!course.getProfessor().getId().equals(professorId)) {
            throw new RuntimeException("Professor is not assigned to this course");
        }
        
        return course.getActiveStudents();
    }
    
    /**
     * Helper method to validate grade data
     */
    private boolean validateGradeData(GradeUploadRequest request) {
        // Check if student ID is provided
        if (request.getStudentId() == null) {
            return false;
        }
        
        // Check if score is valid (0-100)
        if (request.getValue() == null || request.getValue() < 0 || request.getValue() > 100){
            return false;
        }
        
        return true;
    }
    
    /**
     * Response and Request classes
     */
    public static class ProfessorDashboardResponse {
        private String professorName;
        private String department;
        private List<Course> assignedCourses;
        private int totalCourses;
        private int totalStudents;
        private String message;
        
        public ProfessorDashboardResponse(String professorName, String department, 
                                        List<Course> assignedCourses, int totalCourses, 
                                        int totalStudents, String message) {
            this.professorName = professorName;
            this.department = department;
            this.assignedCourses = assignedCourses;
            this.totalCourses = totalCourses;
            this.totalStudents = totalStudents;
            this.message = message;
        }
        
        // Getters
        public String getProfessorName() { return professorName; }
        public String getDepartment() { return department; }
        public List<Course> getAssignedCourses() { return assignedCourses; }
        public int getTotalCourses() { return totalCourses; }
        public int getTotalStudents() { return totalStudents; }
        public String getMessage() { return message; }
    }
    
    public static class CourseGradesResponse {
        private Course course;
        private List<Enrollment> enrollments;
        private List<Grade> grades;
        private Double averageGPA;
        private String message;
        
        public CourseGradesResponse(Course course, List<Enrollment> enrollments, 
                                  List<Grade> grades, Double averageGPA, String message) {
            this.course = course;
            this.enrollments = enrollments;
            this.grades = grades;
            this.averageGPA = averageGPA;
            this.message = message;
        }
        
        // Getters
        public Course getCourse() { return course; }
        public List<Enrollment> getEnrollments() { return enrollments; }
        public List<Grade> getGrades() { return grades; }
        public Double getAverageGPA() { return averageGPA; }
        public String getMessage() { return message; }
    }
    
    public static class GradeUploadRequest {
        private Long studentId;
        private Double score;
        
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        
        public Double getValue() { return score; }
        public void setScore(Double score) { this.score = score; }
    }
    
    public static class GradeUploadResponse {
        private boolean success;
        private String message;
        
        public GradeUploadResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
    
    public static class GradeUpdateResponse {
        private boolean success;
        private String message;
        
        public GradeUpdateResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}
