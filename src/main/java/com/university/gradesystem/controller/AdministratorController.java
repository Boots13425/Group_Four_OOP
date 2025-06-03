package com.university.gradesystem.controller;

import com.university.gradesystem.model.Administrator;
import com.university.gradesystem.model.Course;
import com.university.gradesystem.model.Enrollment;
import com.university.gradesystem.model.SystemConfig;
import com.university.gradesystem.model.User;
import com.university.gradesystem.service.AdministratorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/administrators")
public class AdministratorController {
    private final AdministratorService administratorService;
    public AdministratorController(AdministratorService administratorService) { this.administratorService = administratorService; }

    @GetMapping
    public List<Administrator> getAll() { return administratorService.getAll(); }

    @GetMapping("/{id}")
    public Administrator getById(@PathVariable Long id) { return administratorService.getById(id).orElse(null); }

    @PostMapping
    public Administrator create(@RequestBody Administrator administrator) { return administratorService.save(administrator); }

    @PutMapping("/{id}")
    public Administrator update(@PathVariable Long id, @RequestBody Administrator administrator) {
        administrator.setUserID(id);
        return administratorService.save(administrator);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { administratorService.delete(id); }

    // Generate reports
    @GetMapping("/reports")
    public String generateReports() {
        return administratorService.generateReports();
    }

    // Manage users (list all users)
    @GetMapping("/manage-users")
    public List<User> manageUsers() {
        return administratorService.manageUsers();
    }

    // Manage courses (list all courses)
    @GetMapping("/manage-courses")
    public List<Course> manageCourses() {
        return administratorService.manageCourses();
    }

    // Configure system
    @PostMapping("/configure-system")
    public SystemConfig configureSystem(@RequestBody SystemConfig config) {
        return administratorService.configureSystem(config);
    }

    // Manage course enrollment (list all enrollments)
    @GetMapping("/manage-enrollments")
    public List<Enrollment> manageCourseEnrollment() {
        return administratorService.manageCourseEnrollment();
    }
}