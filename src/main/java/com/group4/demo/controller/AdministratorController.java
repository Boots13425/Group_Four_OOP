package com.group4.demo.controller;

import com.group4.demo.service.AdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Administrator Controller - Handles all administrator-related REST API endpoints
 * This implements all the administrator scenarios from your sequence diagrams
 */
@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdministratorController {
    
    @Autowired
    private AdministratorService administratorService;
    
    /**
     * MANAGE USERS SCENARIO - Create Student
     * POST /admin/users/students
     */
    @PostMapping("/users/students")
    public ResponseEntity<?> createStudent(@RequestBody AdministratorService.CreateStudentRequest request) {
        try {
            AdministratorService.UserManagementResponse response = administratorService
                .createStudent(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AdministratorService.UserManagementResponse(false, e.getMessage()));
        }
    }
    
    /**
     * MANAGE USERS SCENARIO - Create Professor
     * POST /admin/users/professors
     */
    @PostMapping("/users/professors")
    public ResponseEntity<?> createProfessor(@RequestBody AdministratorService.CreateProfessorRequest request) {
        try {
            AdministratorService.UserManagementResponse response = administratorService
                .createProfessor(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AdministratorService.UserManagementResponse(false, e.getMessage()));
        }
    }
    
    /**
     * MANAGE USERS SCENARIO - Create Administrator
     * POST /admin/users/administrators
     */
    @PostMapping("/users/administrators")
    public ResponseEntity<?> createAdministrator(@RequestBody AdministratorService.CreateAdminRequest request) {
        try {
            AdministratorService.UserManagementResponse response = administratorService
                .createAdministrator(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AdministratorService.UserManagementResponse(false, e.getMessage()));
        }
    }
    
    /**
     * MANAGE USERS SCENARIO - Update User
     * PUT /admin/users/{userId}
     */
    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @RequestBody AdministratorService.UpdateUserRequest request) {
        try {
            AdministratorService.UserManagementResponse response = administratorService
                .updateUser(userId, request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AdministratorService.UserManagementResponse(false, e.getMessage()));
        }
    }
    
    /**
     * MANAGE USERS SCENARIO - Update Student
     * PUT /admin/users/students/{userId}
     */
    @PutMapping("/users/students/{userId}")
    public ResponseEntity<?> updateStudent(
            @PathVariable Long userId,
            @RequestBody AdministratorService.UpdateStudentRequest request) {
        try {
            AdministratorService.UserManagementResponse response = administratorService
                .updateUser(userId, request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AdministratorService.UserManagementResponse(false, e.getMessage()));
        }
    }
    
    /**
     * MANAGE USERS SCENARIO - Update Professor
     * PUT /admin/users/professors/{userId}
     */
    @PutMapping("/users/professors/{userId}")
    public ResponseEntity<?> updateProfessor(
            @PathVariable Long userId,
            @RequestBody AdministratorService.UpdateProfessorRequest request) {
        try {
            AdministratorService.UserManagementResponse response = administratorService
                .updateUser(userId, request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AdministratorService.UserManagementResponse(false, e.getMessage()));
        }
    }
    
    /**
     * MANAGE USERS SCENARIO - Update Administrator
     * PUT /admin/users/administrators/{userId}
     */
    @PutMapping("/users/administrators/{userId}")
    public ResponseEntity<?> updateAdministrator(
            @PathVariable Long userId,
            @RequestBody AdministratorService.UpdateAdminRequest request) {
        try {
            AdministratorService.UserManagementResponse response = administratorService
                .updateUser(userId, request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AdministratorService.UserManagementResponse(false, e.getMessage()));
        }
    }
    
    /**
     * MANAGE USERS SCENARIO - Deactivate User
     * DELETE /admin/users/{userId}
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deactivateUser(@PathVariable Long userId) {
        try {
            AdministratorService.UserManagementResponse response = administratorService
                .deactivateUser(userId);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AdministratorService.UserManagementResponse(false, e.getMessage()));
        }
    }
    
    /**
     * MANAGE COURSES SCENARIO - Create Course
     * POST /admin/courses
     */
    @PostMapping("/courses")
    public ResponseEntity<?> createCourse(@RequestBody AdministratorService.CreateCourseRequest request) {
        try {
            AdministratorService.CourseManagementResponse response = administratorService
                .createCourse(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AdministratorService.CourseManagementResponse(false, e.getMessage()));
        }
    }
    
    /**
     * MANAGE COURSES SCENARIO - Update Course
     * PUT /admin/courses/{courseId}
     */
    @PutMapping("/courses/{courseId}")
    public ResponseEntity<?> updateCourse(
            @PathVariable Long courseId,
            @RequestBody AdministratorService.UpdateCourseRequest request) {
        try {
            AdministratorService.CourseManagementResponse response = administratorService
                .updateCourse(courseId, request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AdministratorService.CourseManagementResponse(false, e.getMessage()));
        }
    }
    
    /**
     * MANAGE COURSES SCENARIO - Deactivate Course
     * DELETE /admin/courses/{courseId}
     */
    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<?> deactivateCourse(@PathVariable Long courseId) {
        try {
            AdministratorService.CourseManagementResponse response = administratorService
                .deactivateCourse(courseId);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AdministratorService.CourseManagementResponse(false, e.getMessage()));
        }
    }
    
    /**
     * MANAGE COURSE ENROLLMENT SCENARIO
     * POST /admin/enrollments
     */
    @PostMapping("/enrollments")
    public ResponseEntity<?> manageEnrollment(@RequestBody AdministratorService.EnrollmentManagementRequest request) {
        try {
            AdministratorService.EnrollmentManagementResponse response = administratorService
                .manageEnrollment(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AdministratorService.EnrollmentManagementResponse(false, e.getMessage()));
        }
    }
    
    /**
     * CONFIGURE SYSTEM SCENARIO
     * PUT /admin/system/config
     */
    @PutMapping("/system/config")
    public ResponseEntity<?> configureSystem(@RequestBody AdministratorService.SystemConfigRequest request) {
        try {
            AdministratorService.SystemConfigResponse response = administratorService
                .configureSystem(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AdministratorService.SystemConfigResponse(false, e.getMessage()));
        }
    }
    
    /**
     * GENERATE REPORTS SCENARIO
     * GET /admin/reports/{reportType}
     */
    @GetMapping("/reports/{reportType}")
    public ResponseEntity<?> generateReport(@PathVariable String reportType) {
        try {
            AdministratorService.ReportResponse response = administratorService
                .generateReport(reportType);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AdministratorService.ReportResponse(false, e.getMessage(), null));
        }
    }
    
    /**
     * Error response class
     */
    public static class ErrorResponse {
        private String error;
        private long timestamp;
        
        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getError() { return error; }
        public long getTimestamp() { return timestamp; }
    }
}
