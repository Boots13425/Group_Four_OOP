package com.university.gradessystem.controller;

import com.university.gradessystem.model.Course;
import com.university.gradessystem.model.GradePolicy;
import com.university.gradessystem.model.SystemConfig;
import com.university.gradessystem.model.User;
import com.university.gradessystem.service.CourseService;
import com.university.gradessystem.service.EnrollmentService;
import com.university.gradessystem.service.SystemConfigService;
import com.university.gradessystem.service.UserService;
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
public class AdminController {

    private final UserService userService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final SystemConfigService systemConfigService;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserService userService, CourseService courseService,
            EnrollmentService enrollmentService, SystemConfigService systemConfigService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.systemConfigService = systemConfigService;
        this.passwordEncoder = passwordEncoder;
    }

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
        try {
            User.Role userRole = User.Role.valueOf("ROLE_" + role.toUpperCase());
            return userService.getUsersByRole(userRole);
        } catch (IllegalArgumentException e) {
            return userService.getAllUsers();
        }
    }

    @PostMapping("/api/users")
    @ResponseBody
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            if (userService.existsByUsername(user.getUsername())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
            }
            if (userService.existsByEmail(user.getEmail())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
            }

            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create user: " + e.getMessage()));
        }
    }

    @PutMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
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
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update user: " + e.getMessage()));
        }
    }

    @DeleteMapping("/api/users/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            if (!userService.getUserById(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete user: " + e.getMessage()));
        }
    }

    @PutMapping("/api/users/{id}/activate")
    @ResponseBody
    public ResponseEntity<?> activateUser(@PathVariable Long id) {
        try {
            userService.activateUser(id);
            return ResponseEntity.ok(Map.of("message", "User activated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to activate user: " + e.getMessage()));
        }
    }

    @PutMapping("/api/users/{id}/deactivate")
    @ResponseBody
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        try {
            userService.deactivateUser(id);
            return ResponseEntity.ok(Map.of("message", "User deactivated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to deactivate user: " + e.getMessage()));
        }
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
        try {
            Course createdCourse = courseService.createCourse(course);
            return ResponseEntity.ok(createdCourse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create course: " + e.getMessage()));
        }
    }

    @PutMapping("/api/courses/{id}")
    @ResponseBody
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        try {
            return courseService.getCourseById(id)
                    .map(existingCourse -> {
                        course.setId(id);
                        // Preserve existing grade policy if not provided
                        if (course.getGradePolicy() == null) {
                            course.setGradePolicy(existingCourse.getGradePolicy());
                        }
                        return ResponseEntity.ok(courseService.updateCourse(course));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update course: " + e.getMessage()));
        }
    }

    @DeleteMapping("/api/courses/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        try {
            if (!courseService.getCourseById(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            courseService.deleteCourse(id);
            return ResponseEntity.ok(Map.of("message", "Course deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete course: " + e.getMessage()));
        }
    }

    @PutMapping("/api/courses/{id}/activate")
    @ResponseBody
    public ResponseEntity<?> activateCourse(@PathVariable Long id) {
        try {
            courseService.activateCourse(id);
            return ResponseEntity.ok(Map.of("message", "Course activated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to activate course: " + e.getMessage()));
        }
    }

    @PutMapping("/api/courses/{id}/deactivate")
    @ResponseBody
    public ResponseEntity<?> deactivateCourse(@PathVariable Long id) {
        try {
            courseService.deactivateCourse(id);
            return ResponseEntity.ok(Map.of("message", "Course deactivated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to deactivate course: " + e.getMessage()));
        }
    }

    // Enrollment Management API endpoints
    @PostMapping("/api/enrollments")
    @ResponseBody
    public ResponseEntity<?> enrollStudent(@RequestParam Long studentId, @RequestParam Long courseId) {
        try {
            return ResponseEntity.ok(enrollmentService.enrollStudent(studentId, courseId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to enroll student: " + e.getMessage()));
        }
    }

    @GetMapping("/api/enrollments")
    @ResponseBody
    public List<Map<String, Object>> getAllEnrollments() {
        return enrollmentService.getAllEnrollments().stream()
                .map(enrollment -> {
                    Map<String, Object> enrollmentData = new HashMap<>();
                    enrollmentData.put("id", enrollment.getId());
                    enrollmentData.put("studentName", enrollment.getStudent().getFullName());
                    enrollmentData.put("courseName", enrollment.getCourse().getTitle());
                    enrollmentData.put("courseCode", enrollment.getCourse().getCourseCode());
                    enrollmentData.put("status", enrollment.getStatus());
                    enrollmentData.put("enrollmentDate", enrollment.getEnrollmentDate());
                    return enrollmentData;
                })
                .toList();
    }

    @DeleteMapping("/api/enrollments/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteEnrollment(@PathVariable Long id) {
        try {
            enrollmentService.deleteEnrollment(id);
            return ResponseEntity.ok(Map.of("message", "Enrollment deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete enrollment: " + e.getMessage()));
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
            courseStats.put("available",
                    course.getCapacity() != null ? course.getCapacity() - courseService.countEnrolledStudents(course)
                            : "Unlimited");
            courseStats.put("waitlist", 0); // Implement waitlist count if needed

            stats.put(course.getCourseCode(), courseStats);
        }

        return stats;
    }

    // Grade Policy API endpoints
    @PostMapping("/api/grade-policy")
    @ResponseBody
    public ResponseEntity<?> saveGradePolicy(@RequestBody GradePolicy policy) {
        try {
            return courseService.getCourseById(policy.getCourse().getId())
                    .map(course -> {
                        course.setGradePolicy(policy);
                        policy.setCourse(course);
                        courseService.updateCourse(course);
                        return ResponseEntity.ok(policy);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to save grade policy: " + e.getMessage()));
        }
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
        try {
            systemConfigService.updateGeneralSettings(universityName, academicYear, currentSemester);
            return ResponseEntity.ok(Map.of("message", "General settings updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update settings: " + e.getMessage()));
        }
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
        try {
            systemConfigService.updateSecuritySettings(
                    requireUppercase, requireLowercase, requireNumbers,
                    requireSpecialChars, minimumLength, sessionTimeout, failedLogins);

            return ResponseEntity.ok(Map.of("message", "Security settings updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to update security settings: " + e.getMessage()));
        }
    }

    @PostMapping("/api/system-config/backup")
    @ResponseBody
    public ResponseEntity<?> updateBackupSettings(
            @RequestParam String backupFrequency,
            @RequestParam String backupTime,
            @RequestParam int retentionPeriod) {
        try {
            systemConfigService.updateBackupSettings(backupFrequency, backupTime, retentionPeriod);
            return ResponseEntity.ok(Map.of("message", "Backup settings updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to update backup settings: " + e.getMessage()));
        }
    }

    @PostMapping("/api/backup/run")
    @ResponseBody
    public ResponseEntity<?> runBackup() {
        try {
            // Simulate backup process
            System.out.println("Running manual backup...");
            Thread.sleep(2000); // Simulate backup time
            return ResponseEntity.ok(Map.of("message", "Backup completed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Backup failed: " + e.getMessage()));
        }
    }

    // Reports and Analytics
    @GetMapping("/api/reports/users")
    @ResponseBody
    public Map<String, Object> getUserReports() {
        Map<String, Object> report = new HashMap<>();
        report.put("totalUsers", userService.getAllUsers().size());
        report.put("activeUsers", userService.getAllUsers().stream().filter(User::isActive).count());
        report.put("students", userService.countStudents());
        report.put("professors", userService.countProfessors());
        report.put("admins", userService.getUsersByRole(User.Role.ROLE_ADMIN).size());
        return report;
    }

    @GetMapping("/api/reports/courses")
    @ResponseBody
    public Map<String, Object> getCourseReports() {
        Map<String, Object> report = new HashMap<>();
        List<Course> allCourses = courseService.getAllCourses();
        report.put("totalCourses", allCourses.size());
        report.put("activeCourses", courseService.countActiveCourses());

        Map<String, Long> departmentStats = allCourses.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Course::getDepartment,
                        java.util.stream.Collectors.counting()));
        report.put("departmentStats", departmentStats);

        return report;
    }
}
