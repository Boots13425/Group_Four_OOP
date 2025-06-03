package com.group4.demo.service;

import com.group4.demo.model.*;
import com.group4.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Administrator Service - Handles all administrator-related business logic
 * This implements the administrator use cases from your sequence diagrams
 */
@Service
@Transactional
public class AdministratorService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private ProfessorRepository professorRepository;
    
    @Autowired
    private AdministratorRepository administratorRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    @Autowired
    private GradeRepository gradeRepository;
    
    @Autowired
    private SystemConfigRepository systemConfigRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * MANAGE USERS SCENARIO
     * This implements the complete user management flow
     */
    
    // Create new student
    public UserManagementResponse createStudent(CreateStudentRequest request) {
        try {
            // Step 1: Validate input data
            if (userRepository.existsByUsername(request.getUsername())) {
                return new UserManagementResponse(false, "Username already exists");
            }
            
            if (studentRepository.existsByStudentId(request.getStudentId())) {
                return new UserManagementResponse(false, "Student ID already exists");
            }
            
            // Step 2: Create student
            Student student = new Student(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getName(),
                request.getEmail(),
                request.getStudentId()
            );
            
            studentRepository.save(student);
            
            return new UserManagementResponse(true, "Student created successfully");
            
        } catch (Exception e) {
            return new UserManagementResponse(false, "Error creating student: " + e.getMessage());
        }
    }
    
    // Create new professor
    public UserManagementResponse createProfessor(CreateProfessorRequest request) {
        try {
            // Step 1: Validate input data
            if (userRepository.existsByUsername(request.getUsername())) {
                return new UserManagementResponse(false, "Username already exists");
            }
            
            if (professorRepository.existsByEmployeeId(request.getEmployeeId())) {
                return new UserManagementResponse(false, "Employee ID already exists");
            }
            
            // Step 2: Create professor
            Professor professor = new Professor(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getName(),
                request.getEmail(),
                request.getEmployeeId(),
                request.getDepartment()
            );
            
            professorRepository.save(professor);
            
            return new UserManagementResponse(true, "Professor created successfully");
            
        } catch (Exception e) {
            return new UserManagementResponse(false, "Error creating professor: " + e.getMessage());
        }
    }
    
    // Create new administrator
    public UserManagementResponse createAdministrator(CreateAdminRequest request) {
        try {
            // Step 1: Validate input data
            if (userRepository.existsByUsername(request.getUsername())) {
                return new UserManagementResponse(false, "Username already exists");
            }
            
            // Step 2: Create administrator
            Administrator admin = new Administrator(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getName(),
                request.getEmail(),
                request.getAdminLevel()
            );
            
            // Set permissions if provided
            if (request.getPermissions() != null) {
                admin.setPermissions(request.getPermissions());
            }
            
            administratorRepository.save(admin);
            
            return new UserManagementResponse(true, "Administrator created successfully");
            
        } catch (Exception e) {
            return new UserManagementResponse(false, "Error creating administrator: " + e.getMessage());
        }
    }
    
    // Update user information
    public UserManagementResponse updateUser(Long userId, UpdateUserRequest request) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Update common fields
            if (request.getName() != null) {
                user.setName(request.getName());
            }
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
            }
            if (request.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            
            // Update role-specific fields
            if (user instanceof Student) {
                Student student = (Student) user;
                if (request instanceof UpdateStudentRequest) {
                    UpdateStudentRequest studentRequest = (UpdateStudentRequest) request;
                    if (studentRequest.getStudentId() != null) {
                        student.setStudentID(studentRequest.getStudentId());
                    }
                }
            } else if (user instanceof Professor) {
                Professor professor = (Professor) user;
                if (request instanceof UpdateProfessorRequest) {
                    UpdateProfessorRequest professorRequest = (UpdateProfessorRequest) request;
                    if (professorRequest.getEmployeeId() != null) {
                        professor.setProfessorID(professorRequest.getEmployeeId());
                    }
                    if (professorRequest.getDepartment() != null) {
                        professor.setDepartment(professorRequest.getDepartment());
                    }
                }
            } else if (user instanceof Administrator) {
                Administrator admin = (Administrator) user;
                if (request instanceof UpdateAdminRequest) {
                    UpdateAdminRequest adminRequest = (UpdateAdminRequest) request;
                    if (adminRequest.getAdminLevel() != null) {
                        admin.setAdminLevel(adminRequest.getAdminLevel());
                    }
                    if (adminRequest.getPermissions() != null) {
                        admin.setPermissions(adminRequest.getPermissions());
                    }
                }
            }
            
            userRepository.save(user);
            
            return new UserManagementResponse(true, "User updated successfully");
            
        } catch (Exception e) {
            return new UserManagementResponse(false, "Error updating user: " + e.getMessage());
        }
    }
    
    // Deactivate user
    public UserManagementResponse deactivateUser(Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            user.setActive(false);
            userRepository.save(user);
            
            return new UserManagementResponse(true, "User deactivated successfully");
            
        } catch (Exception e) {
            return new UserManagementResponse(false, "Error deactivating user: " + e.getMessage());
        }
    }
    
    /**
     * MANAGE COURSES SCENARIO
     * This implements the complete course management flow
     */
    
    // Create new course
    public CourseManagementResponse createCourse(CreateCourseRequest request) {
        try {
            // Step 1: Validate input data
            if (courseRepository.existsByCourseCode(request.getCourseCode())) {
                return new CourseManagementResponse(false, "Course code already exists");
            }
            
            // Step 2: Find professor
            Professor professor = professorRepository.findById(request.getProfessorId())
                .orElseThrow(() -> new RuntimeException("Professor not found"));



            // Step 3: Create course
            Course course = new Course(

                request.getCourseCode(),
                request.getTitle(),
                request.getCredits(),
                request.getDescription(), // this just a trial to check
                request.getCapacity(),
                professor
            );



            courseRepository.save(course);
            
            return new CourseManagementResponse(true, "Course created successfully");
            
        } catch (Exception e) {
            return new CourseManagementResponse(false, "Error creating course: " + e.getMessage());
        }
    }
    
    // Update course
    public CourseManagementResponse updateCourse(Long courseId, UpdateCourseRequest request) {
        try {
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
            
            // Update fields
            if (request.getTitle() != null) {
                course.setCourseName(request.getTitle());
            }
            if (request.getDescription() != null) {
                course.setDescription(request.getDescription());
            }
            if (request.getCapacity() != null) {
                course.setMaxCapacity(request.getCapacity());
            }
            if (request.getProfessorId() != null) {
                Professor professor = professorRepository.findById(request.getProfessorId())
                    .orElseThrow(() -> new RuntimeException("Professor not found"));
                course.setProfessor(professor);
            }
            
            courseRepository.save(course);
            
            return new CourseManagementResponse(true, "Course updated successfully");
            
        } catch (Exception e) {
            return new CourseManagementResponse(false, "Error updating course: " + e.getMessage());
        }
    }
    
    // Deactivate course
    public CourseManagementResponse deactivateCourse(Long courseId) {
        try {
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
            
            course.setActive(false);
            courseRepository.save(course);
            
            return new CourseManagementResponse(true, "Course deactivated successfully");
            
        } catch (Exception e) {
            return new CourseManagementResponse(false, "Error deactivating course: " + e.getMessage());
        }
    }
    
    /**
     * MANAGE COURSE ENROLLMENT SCENARIO
     * This implements the enrollment management flow
     */
    public EnrollmentManagementResponse manageEnrollment(EnrollmentManagementRequest request) {
        try {
            switch (request.getAction().toUpperCase()) {
                case "ENROLL":
                    return enrollStudentInCourse(request.getStudentId(), request.getCourseId(), request.getTerm());
                case "DROP":
                    return dropStudentFromCourse(request.getStudentId(), request.getCourseId());
                case "TRANSFER":
                    return transferStudentToCourse(request.getStudentId(), request.getCourseId(), request.getNewCourseId());
                default:
                    return new EnrollmentManagementResponse(false, "Invalid action: " + request.getAction());
            }
        } catch (Exception e) {
            return new EnrollmentManagementResponse(false, "Error managing enrollment: " + e.getMessage());
        }
    }
    
    private EnrollmentManagementResponse enrollStudentInCourse(Long studentId, Long courseId, String term) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));
        
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found"));
        
        // Check if already enrolled
        Optional<Enrollment> existingEnrollment = enrollmentRepository
            .findActiveEnrollment(studentId, courseId);
        if (existingEnrollment.isPresent()) {
            return new EnrollmentManagementResponse(false, "Student is already enrolled in this course");
        }
        
        // Check capacity
        if (!course.hasAvailableSlots()) {
            return new EnrollmentManagementResponse(false, "Course is full");
        }
        
        // Create enrollment
        Enrollment enrollment = new Enrollment(student, course, term);
        enrollmentRepository.save(enrollment);
        
        return new EnrollmentManagementResponse(true, "Student enrolled successfully");
    }
    
    private EnrollmentManagementResponse dropStudentFromCourse(Long studentId, Long courseId) {
        Optional<Enrollment> enrollmentOpt = enrollmentRepository
            .findActiveEnrollment(studentId, courseId);
        
        if (enrollmentOpt.isEmpty()) {
            return new EnrollmentManagementResponse(false, "Student is not enrolled in this course");
        }
        
        Enrollment enrollment = enrollmentOpt.get();
        enrollment.dropCourse();
        enrollmentRepository.save(enrollment);
        
        return new EnrollmentManagementResponse(true, "Student dropped from course successfully");
    }
    
    private EnrollmentManagementResponse transferStudentToCourse(Long studentId, Long oldCourseId, Long newCourseId) {
        // Drop from old course
        EnrollmentManagementResponse dropResponse = dropStudentFromCourse(studentId, oldCourseId);
        if (!dropResponse.isSuccess()) {
            return dropResponse;
        }
        
        // Enroll in new course
        return enrollStudentInCourse(studentId, newCourseId, "CURRENT");
    }
    
    /**
     * CONFIGURE SYSTEM SCENARIO
     * This implements the system configuration flow
     */
    public SystemConfigResponse configureSystem(SystemConfigRequest request) {
        try {
            SystemConfig config = systemConfigRepository.findCurrentConfig()
                .orElse(new SystemConfig());
            
            // Update configuration fields
            if (request.getBackupTime() != null) {
                config.setBackupTime(request.getBackupTime());
            }
            if (request.getAdminEmail() != null) {
                config.setAdminEmail(request.getAdminEmail());
            }
            if (request.getMaxLoginAttempts() != null) {
                config.setMaxLoginAttempts(request.getMaxLoginAttempts());
            }
            if (request.getSessionTimeoutMinutes() != null) {
                config.setSessionTimeoutMinutes(request.getSessionTimeoutMinutes());
            }
            if (request.getGradeReleaseEnabled() != null) {
                config.setGradeReleaseEnabled(request.getGradeReleaseEnabled());
            }
            if (request.getEnrollmentEnabled() != null) {
                config.setEnrollmentEnabled(request.getEnrollmentEnabled());
            }
            
            config.updateConfig("Administrator");
            systemConfigRepository.save(config);
            
            return new SystemConfigResponse(true, "System configuration updated successfully");
            
        } catch (Exception e) {
            return new SystemConfigResponse(false, "Error updating system configuration: " + e.getMessage());
        }
    }
    
    /**
     * GENERATE REPORTS SCENARIO
     * This implements the report generation flow
     */
    public ReportResponse generateReport(String reportType) {
        try {
            switch (reportType.toUpperCase()) {
                case "STUDENT_PERFORMANCE":
                    return generateStudentPerformanceReport();
                case "COURSE_ENROLLMENT":
                    return generateCourseEnrollmentReport();
                case "GRADE_DISTRIBUTION":
                    return generateGradeDistributionReport();
                case "SYSTEM_STATISTICS":
                    return generateSystemStatisticsReport();
                default:
                    return new ReportResponse(false, "Unknown report type: " + reportType, null);
            }
        } catch (Exception e) {
            return new ReportResponse(false, "Error generating report: " + e.getMessage(), null);
        }
    }
    
    private ReportResponse generateStudentPerformanceReport() {
        List<Student> topPerformers = studentRepository.findTopPerformers();
        
        StringBuilder report = new StringBuilder();
        report.append("STUDENT PERFORMANCE REPORT\n");
        report.append("Generated: ").append(LocalDateTime.now()).append("\n\n");
        report.append("Top Performing Students:\n");
        
        for (int i = 0; i < Math.min(10, topPerformers.size()); i++) {
            Student student = topPerformers.get(i);
            report.append(String.format("%d. %s (ID: %s) - GPA: %.2f\n", 
                i + 1, student.getName(), student.getStudentID(), student.getCurrentGPA()));
        }
        
        return new ReportResponse(true, "Student performance report generated successfully", report.toString());
    }
    
    private ReportResponse generateCourseEnrollmentReport() {
        List<Course> courses = courseRepository.findByIsActiveTrue();
        
        StringBuilder report = new StringBuilder();
        report.append("COURSE ENROLLMENT REPORT\n");
        report.append("Generated: ").append(LocalDateTime.now()).append("\n\n");
        
        for (Course course : courses) {
            double enrollmentPercentage = (double) course.getCurrentEnrollment() / course.getMaxCapacity() * 100;
            report.append(String.format("Course: %s (%s)\n", course.getCourseName(), course.getCourseID()));
            report.append(String.format("Enrollment: %d/%d (%.1f%%)\n", 
                course.getCurrentEnrollment(), course.getMaxCapacity(), enrollmentPercentage));
            report.append(String.format("Professor: %s\n\n", course.getProfessor().getName()));
        }
        
        return new ReportResponse(true, "Course enrollment report generated successfully", report.toString());
    }
    
    private ReportResponse generateGradeDistributionReport() {
        StringBuilder report = new StringBuilder();
        report.append("GRADE DISTRIBUTION REPORT\n");
        report.append("Generated: ").append(LocalDateTime.now()).append("\n\n");
        
        String[] letterGrades = {"A", "B+", "B", "C+", "C", "D+", "D", "F"};
        
        for (String letterGrade : letterGrades) {
            List<Grade> grades = gradeRepository.findByLetterGrade(letterGrade);
            report.append(String.format("Grade %s: %d students\n", letterGrade, grades.size()));
        }
        
        return new ReportResponse(true, "Grade distribution report generated successfully", report.toString());
    }
    
    private ReportResponse generateSystemStatisticsReport() {
        long totalStudents = studentRepository.count();
        long totalProfessors = professorRepository.count();
        long totalCourses = courseRepository.count();
        long totalEnrollments = enrollmentRepository.count();
        long totalGrades = gradeRepository.count();
        
        StringBuilder report = new StringBuilder();
        report.append("SYSTEM STATISTICS REPORT\n");
        report.append("Generated: ").append(LocalDateTime.now()).append("\n\n");
        report.append("Total Students: ").append(totalStudents).append("\n");
        report.append("Total Professors: ").append(totalProfessors).append("\n");
        report.append("Total Courses: ").append(totalCourses).append("\n");
        report.append("Total Enrollments: ").append(totalEnrollments).append("\n");
        report.append("Total Grades: ").append(totalGrades).append("\n");
        
        return new ReportResponse(true, "System statistics report generated successfully", report.toString());
    }
    
    /**
     * Request and Response classes
     */
    public static class CreateStudentRequest {
        private String username;
        private String password;
        private String name;
        private String email;
        private String studentId;
        
        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
    }
    
    public static class CreateProfessorRequest {
        private String username;
        private String password;
        private String name;
        private String email;
        private String employeeId;
        private String department;
        
        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getEmployeeId() { return employeeId; }
        public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
    }
    
    public static class CreateAdminRequest {
        private String username;
        private String password;
        private String name;
        private String email;
        private String adminLevel;
        private String permissions;
        
        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getAdminLevel() { return adminLevel; }
        public void setAdminLevel(String adminLevel) { this.adminLevel = adminLevel; }
        
        public String getPermissions() { return permissions; }
        public void setPermissions(String permissions) { this.permissions = permissions; }
    }
    
    public static class UpdateUserRequest {
        private String name;
        private String email;
        private String password;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class UpdateStudentRequest extends UpdateUserRequest {
        private String studentId;
        
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
    }
    
    public static class UpdateProfessorRequest extends UpdateUserRequest {
        private String employeeId;
        private String department;
        
        public String getEmployeeId() { return employeeId; }
        public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
    }
    
    public static class UpdateAdminRequest extends UpdateUserRequest {
        private String adminLevel;
        private String permissions;
        
        public String getAdminLevel() { return adminLevel; }
        public void setAdminLevel(String adminLevel) { this.adminLevel = adminLevel; }
        
        public String getPermissions() { return permissions; }
        public void setPermissions(String permissions) { this.permissions = permissions; }
    }
    
    public static class CreateCourseRequest {
        private String courseCode;
        private String title;
        private String description;
        private Integer credits;
        private Integer capacity;
        private Long professorId;
        
        // Getters and setters
        public String getCourseCode() { return courseCode; }
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Integer getCredits() { return credits; }
        public void setCredits(Integer credits) { this.credits = credits; }
        
        public Integer getCapacity() { return capacity; }
        public void setCapacity(Integer capacity) { this.capacity = capacity; }
        
        public Long getProfessorId() { return professorId; }
        public void setProfessorId(Long professorId) { this.professorId = professorId; }
    }
    
    public static class UpdateCourseRequest {
        private String title;
        private String description;
        private Integer capacity;
        private Long professorId;
        
        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Integer getCapacity() { return capacity; }
        public void setCapacity(Integer capacity) { this.capacity = capacity; }
        
        public Long getProfessorId() { return professorId; }
        public void setProfessorId(Long professorId) { this.professorId = professorId; }
    }
    
    public static class EnrollmentManagementRequest {
        private String action; // ENROLL, DROP, TRANSFER
        private Long studentId;
        private Long courseId;
        private Long newCourseId; // For transfer
        private String term;
        
        // Getters and setters
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        
        public Long getCourseId() { return courseId; }
        public void setCourseId(Long courseId) { this.courseId = courseId; }
        
        public Long getNewCourseId() { return newCourseId; }
        public void setNewCourseId(Long newCourseId) { this.newCourseId = newCourseId; }
        
        public String getTerm() { return term; }
        public void setTerm(String term) { this.term = term; }
    }
    
    public static class SystemConfigRequest {
        private java.time.LocalTime backupTime;
        private String adminEmail;
        private Integer maxLoginAttempts;
        private Integer sessionTimeoutMinutes;
        private Boolean gradeReleaseEnabled;
        private Boolean enrollmentEnabled;
        
        // Getters and setters
        public java.time.LocalTime getBackupTime() { return backupTime; }
        public void setBackupTime(java.time.LocalTime backupTime) { this.backupTime = backupTime; }
        
        public String getAdminEmail() { return adminEmail; }
        public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
        
        public Integer getMaxLoginAttempts() { return maxLoginAttempts; }
        public void setMaxLoginAttempts(Integer maxLoginAttempts) { this.maxLoginAttempts = maxLoginAttempts; }
        
        public Integer getSessionTimeoutMinutes() { return sessionTimeoutMinutes; }
        public void setSessionTimeoutMinutes(Integer sessionTimeoutMinutes) { this.sessionTimeoutMinutes = sessionTimeoutMinutes; }
        
        public Boolean getGradeReleaseEnabled() { return gradeReleaseEnabled; }
        public void setGradeReleaseEnabled(Boolean gradeReleaseEnabled) { this.gradeReleaseEnabled = gradeReleaseEnabled; }
        
        public Boolean getEnrollmentEnabled() { return enrollmentEnabled; }
        public void setEnrollmentEnabled(Boolean enrollmentEnabled) { this.enrollmentEnabled = enrollmentEnabled; }
    }
    
    // Response classes
    public static class UserManagementResponse {
        private boolean success;
        private String message;
        
        public UserManagementResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
    
    public static class CourseManagementResponse {
        private boolean success;
        private String message;
        
        public CourseManagementResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
    
    public static class EnrollmentManagementResponse {
        private boolean success;
        private String message;
        
        public EnrollmentManagementResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
    
    public static class SystemConfigResponse {
        private boolean success;
        private String message;
        
        public SystemConfigResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
    
    public static class ReportResponse {
        private boolean success;
        private String message;
        private String reportData;
        
        public ReportResponse(boolean success, String message, String reportData) {
            this.success = success;
            this.message = message;
            this.reportData = reportData;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getReportData() { return reportData; }
    }
}
