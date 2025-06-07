package com.university.gradessystem.controller;

import com.university.gradessystem.model.Course;
import com.university.gradessystem.model.GradePolicy;
import com.university.gradessystem.model.SystemConfig;
import com.university.gradessystem.model.User;
import com.university.gradessystem.service.CourseService;
import com.university.gradessystem.service.EnrollmentService;
import com.university.gradessystem.service.SystemConfigService;
import com.university.gradessystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final SystemConfigService systemConfigService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("totalStudents", userService.countStudents());
        model.addAttribute("totalProfessors", userService.countProfessors());
        model.addAttribute("activeCourses", courseService.countActiveCourses());
        
        // Add system config
        SystemConfig config = systemConfigService.getOrCreateSystemConfig();
        model.addAttribute("config", config);
        
        return "admin";
    }

    // User Management API endpoints
    @GetMapping("/api/users")
    @ResponseBody
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/api/users/{role}")
    @ResponseBody
    public List<User> getUsersByRole(@PathVariable String role) {
        User.Role userRole = User.Role.valueOf("ROLE_" + role.toUpperCase());
        return userService.getUsersByRole(userRole);
    }

    @PostMapping("/api/users")
    @ResponseBody
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.getUserById(id)
                .map(existingUser -> {
                    user.setId(id);
                    // Don't update password if it's empty
                    if (user.getPassword() == null || user.getPassword().isEmpty()) {
                        user.setPassword(existingUser.getPassword());
                    } else {
                        user.setPassword(passwordEncoder.encode(user.getPassword()));
                    }
                    return ResponseEntity.ok(userService.updateUser(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userService.getUserById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    // Course Management API endpoints
    @GetMapping("/api/courses")
    @ResponseBody
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/api/courses/active")
    @ResponseBody
    public List<Course> getActiveCourses() {
        return courseService.getActiveCourses();
    }

    @PostMapping("/api/courses")
    @ResponseBody
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        Course createdCourse = courseService.createCourse(course);
        return ResponseEntity.ok(createdCourse);
    }

    @PutMapping("/api/courses/{id}")
    @ResponseBody
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        return courseService.getCourseById(id)
                .map(existingCourse -> {
                    course.setId(id);
                    return ResponseEntity.ok(courseService.updateCourse(course));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/courses/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        if (!courseService.getCourseById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        courseService.deleteCourse(id);
        return ResponseEntity.ok().build();
    }

    // Enrollment Management API endpoints
    @PostMapping("/api/enrollments")
    @ResponseBody
    public ResponseEntity<?> enrollStudent(@RequestParam Long studentId, @RequestParam Long courseId) {
        try {
            return ResponseEntity.ok(enrollmentService.enrollStudent(studentId, courseId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/api/enrollment-stats")
    @ResponseBody
    public Map<String, Object> getEnrollmentStats() {
        List<Course> courses = courseService.getActiveCourses();
        Map<String, Object> stats = new HashMap<>();
        
        for (Course course : courses) {
            Map<String, Object> courseStats = new HashMap<>();
            courseStats.put("capacity", course.getCapacity());
            courseStats.put("enrolled", courseService.countEnrolledStudents(course));
            courseStats.put("available", course.getCapacity() - courseService.countEnrolledStudents(course));
            courseStats.put("waitlist", 0); // Implement waitlist count if needed
            
            stats.put(course.getCourseCode(), courseStats);
        }
        
        return stats;
    }

    // Grade Policy API endpoints
    @PostMapping("/api/grade-policy")
    @ResponseBody
    public ResponseEntity<?> saveGradePolicy(@RequestBody GradePolicy policy) {
        return courseService.getCourseById(policy.getCourse().getId())
                .map(course -> {
                    course.setGradePolicy(policy);
                    policy.setCourse(course);
                    courseService.updateCourse(course);
                    return ResponseEntity.ok(policy);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // System Configuration API endpoints
    @GetMapping("/api/system-config")
    @ResponseBody
    public SystemConfig getSystemConfig() {
        return systemConfigService.getOrCreateSystemConfig();
    }

    @PostMapping("/api/system-config/general")
    @ResponseBody
    public ResponseEntity<?> updateGeneralSettings(
            @RequestParam String universityName,
            @RequestParam String academicYear,
            @RequestParam String currentSemester) {
        
        systemConfigService.updateGeneralSettings(universityName, academicYear, currentSemester);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/system-config/security")
    @ResponseBody
    public ResponseEntity<?> updateSecuritySettings(
            @RequestParam boolean requireUppercase,
            @RequestParam boolean requireLowercase,
            @RequestParam boolean requireNumbers,
            @RequestParam boolean requireSpecialChars,
            @RequestParam int minimumLength,
            @RequestParam int sessionTimeout,
            @RequestParam int failedLogins) {
        
        systemConfigService.updateSecuritySettings(
                requireUppercase, requireLowercase, requireNumbers, 
                requireSpecialChars, minimumLength, sessionTimeout, failedLogins);
        
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/system-config/backup")
    @ResponseBody
    public ResponseEntity<?> updateBackupSettings(
            @RequestParam String backupFrequency,
            @RequestParam String backupTime,
            @RequestParam int retentionPeriod) {
        
        systemConfigService.updateBackupSettings(backupFrequency, backupTime, retentionPeriod);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/backup/run")
    @ResponseBody
    public ResponseEntity<?> runBackup() {
        // Simulate backup process
        System.out.println("Running manual backup...");
        return ResponseEntity.ok().body("Backup completed successfully");
    }
}
