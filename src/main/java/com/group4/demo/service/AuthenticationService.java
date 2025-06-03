package com.group4.demo.service;

import com.group4.demo.model.*;
import com.group4.demo.model.UserRole;
import com.group4.demo.repository.*;
import com.group4.demo.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Authentication Service - Handles user login and token generation
 * This implements all login scenarios with role-specific functions
 */
@Service
public class AuthenticationService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private ProfessorRepository professorRepository;
    
    @Autowired
    private AdministratorRepository administratorRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    /**
     * Authenticate user of any role and generate JWT token
     */
    public LoginResponse authenticateUser(String username, String password) {
        try {
            // Step 1: Validate credentials using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            
            // Step 2: Find user in database
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isEmpty()) {
                throw new RuntimeException("User not found");
            }
            
            User user = userOptional.get();
            
            // Step 3: Check if user account is active
            if (!user.isActive()) {
                throw new RuntimeException("Account is deactivated");
            }
            
            // Step 4: Generate JWT token (session token)
            String token = jwtTokenProvider.generateToken(authentication);
            
            // Step 5: Return successful login response
            return new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getRole().toString(),
                "Login successful"
            );
            
        } catch (AuthenticationException e) {
            // Handle failed login attempts
            throw new RuntimeException("Invalid username or password");
        }
    }
    
    /**
     * Authenticate student and generate JWT token
     */
    public LoginResponse authenticateStudent(String username, String password) {
        try {
            // Step 1: Validate credentials using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            
            // Step 2: Find student in database
            Optional<Student> studentOptional = studentRepository.findByUsername(username);
            if (studentOptional.isEmpty()) {
                throw new RuntimeException("Student account not found");
            }
            
            Student student = studentOptional.get();
            
            // Step 3: Check if account is active
            if (!student.isActive()) {
                throw new RuntimeException("Student account is deactivated");
            }
            
            // Step 4: Verify the user is actually a student
            if (!student.getRole().equals(UserRole.STUDENT.name())) {
                throw new RuntimeException("Invalid student credentials");
            }
            
            // Step 5: Generate JWT token (session token)
            String token = jwtTokenProvider.generateToken(authentication);
            
            // Step 6: Load student-specific data for dashboard
            int currentEnrollments = student.getEnrollments().size();
            Double gpa = (double) student.getCurrentGPA();
            
            // Step 7: Return successful login response with student info
            return new StudentLoginResponse(
                token,
                student.getId(),
                student.getUsername(),
                student.getName(),
                student.getEmail(),
                UserRole.STUDENT.toString(),
                "Student login successful",
                student.getStudentID(),
                gpa,
                student.getTotalCredits(),
                currentEnrollments
            );
            
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid student credentials");
        }
    }
    
    /**
     * Authenticate professor and generate JWT token
     */
    public LoginResponse authenticateProfessor(String username, String password) {
        try {
            // Step 1: Validate credentials using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            
            // Step 2: Find professor in database
            Optional<Professor> professorOptional = professorRepository.findByUsername(username);
            if (professorOptional.isEmpty()) {
                throw new RuntimeException("Professor account not found");
            }
            
            Professor professor = professorOptional.get();
            
            // Step 3: Check if account is active
            if (!professor.isActive()) {
                throw new RuntimeException("Professor account is deactivated");
            }
            
            // Step 4: Verify the user is actually a professor

            if (!professor.getRole().equals(UserRole.PROFESSOR)){
                throw new RuntimeException("Invalid professor credentials");
            }
            
            // Step 5: Generate JWT token (session token)
            String token = jwtTokenProvider.generateToken(authentication);
            
            // Step 6: Load professor-specific data for dashboard
            int courseCount = professor.getCourses().size();
            
            // Step 7: Return successful login response with professor info
            return new ProfessorLoginResponse(
                token,
                professor.getId(),
                professor.getUsername(),
                professor.getName(),
                professor.getEmail(),
                UserRole.PROFESSOR.toString(),
                "Professor login successful",
                professor.getProfessorID(),
                professor.getDepartment(),
                courseCount
            );
            
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid professor credentials");
        }
    }
    
    /**
     * Authenticate administrator and generate JWT token
     */
    public LoginResponse authenticateAdmin(String username, String password) {
        try {
            // Step 1: Validate credentials using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            
            // Step 2: Find administrator in database
            Optional<Administrator> adminOptional = administratorRepository.findByUsername(username);
            if (adminOptional.isEmpty()) {
                throw new RuntimeException("Administrator account not found");
            }
            
            Administrator admin = adminOptional.get();
            
            // Step 3: Check if account is active
            if (!admin.isActive()) {
                throw new RuntimeException("Administrator account is deactivated");
            }
            
            // Step 4: Verify the user is actually an administrator

            if (!admin.getRole().equals(UserRole.ADMINISTRATOR)) {
                throw new RuntimeException("Invalid administrator credentials");
            }
            
            // Step 5: Generate JWT token (session token)
            String token = jwtTokenProvider.generateToken(authentication);
            
            // Step 6: Return successful login response with admin info
            return new AdminLoginResponse(
                token,
                admin.getId(),
                admin.getUsername(),
                admin.getName(),
                admin.getEmail(),
                UserRole.ADMINISTRATOR.toString(),
                "Administrator login successful",
                admin.getAdminLevel(),
                admin.getPermissions()
            );
            
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid administrator credentials");
        }
    }
    
    /**
     * Validate JWT token and get user info
     */
    public User validateTokenAndGetUser(String token) {
        if (jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("Invalid token");
    }
    
    /**
     * Check if user has permission to access resource
     */
    public boolean hasPermission(User user, String resource, Long resourceId) {
        switch (user.getRole()) {
            case "STUDENT":
                // Students can only access their own data
                return user.getId().equals(resourceId);
            case "PROFESSOR":
                // Professors can access their courses and students
                return true; // Additional logic would be implemented here
            case "ADMINISTRATOR":
                // Admins have full access
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Base login response class
     */
    public static class LoginResponse {
        private String token;
        private Long userId;
        private String username;
        private String name;
        private String email;
        private String role;
        private String message;
        
        public LoginResponse(String token, Long userId, String username, String name, 
                           String email, String role, String message) {
            this.token = token;
            this.userId = userId;
            this.username = username;
            this.name = name;
            this.email = email;
            this.role = role;
            this.message = message;
        }
        
        // Getters and setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    /**
     * Student-specific login response with additional student data
     */
    public static class StudentLoginResponse extends LoginResponse {
        private String studentId;
        private Double currentGPA;
        private Integer totalCredits;
        private Integer currentEnrollments;
        
        public StudentLoginResponse(String token, Long userId, String username, String name,
                                  String email, String role, String message,
                                  String studentId, Double currentGPA, Integer totalCredits,
                                  Integer currentEnrollments) {
            super(token, userId, username, name, email, role, message);
            this.studentId = studentId;
            this.currentGPA = currentGPA;
            this.totalCredits = totalCredits;
            this.currentEnrollments = currentEnrollments;
        }
        
        // Getters and setters
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        
        public Double getCurrentGPA() { return currentGPA; }
        public void setCurrentGPA(Double currentGPA) { this.currentGPA = currentGPA; }
        
        public Integer getTotalCredits() { return totalCredits; }
        public void setTotalCredits(Integer totalCredits) { this.totalCredits = totalCredits; }
        
        public Integer getCurrentEnrollments() { return currentEnrollments; }
        public void setCurrentEnrollments(Integer currentEnrollments) { this.currentEnrollments = currentEnrollments; }
    }
    
    /**
     * Professor-specific login response with additional professor data
     */
    public static class ProfessorLoginResponse extends LoginResponse {
        private String employeeId;
        private String department;
        private Integer courseCount;
        
        public ProfessorLoginResponse(String token, Long userId, String username, String name,
                                    String email, String role, String message,
                                    String employeeId, String department, Integer courseCount) {
            super(token, userId, username, name, email, role, message);
            this.employeeId = employeeId;
            this.department = department;
            this.courseCount = courseCount;
        }
        
        // Getters and setters
        public String getEmployeeId() { return employeeId; }
        public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        
        public Integer getCourseCount() { return courseCount; }
        public void setCourseCount(Integer courseCount) { this.courseCount = courseCount; }
    }
    
    /**
     * Admin-specific login response with additional admin data
     */
    public static class AdminLoginResponse extends LoginResponse {
        private String adminLevel;
        private String permissions;
        
        public AdminLoginResponse(String token, Long userId, String username, String name,
                                String email, String role, String message,
                                String adminLevel, String permissions) {
            super(token, userId, username, name, email, role, message);
            this.adminLevel = adminLevel;
            this.permissions = permissions;
        }
        
        // Getters and setters
        public String getAdminLevel() { return adminLevel; }
        public void setAdminLevel(String adminLevel) { this.adminLevel = adminLevel; }
        
        public String getPermissions() { return permissions; }
        public void setPermissions(String permissions) { this.permissions = permissions; }
    }
}
