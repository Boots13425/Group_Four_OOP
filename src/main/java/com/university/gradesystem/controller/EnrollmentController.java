package com.university.gradesystem.controller;

import com.university.gradesystem.model.Enrollment;
import com.university.gradesystem.service.EnrollmentService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    public EnrollmentController(EnrollmentService enrollmentService) { this.enrollmentService = enrollmentService; }

    @GetMapping
    public List<Enrollment> getAll() { return enrollmentService.getAll(); }

    @GetMapping("/{id}")
    public Enrollment getById(@PathVariable Long id) { return enrollmentService.getById(id); }

    @PostMapping
    public Enrollment create(@RequestBody Enrollment enrollment) { return enrollmentService.save(enrollment); }

    @PutMapping("/{id}")
    public Enrollment update(@PathVariable Long id, @RequestBody Enrollment enrollment) {
        enrollment.setEnrollmentID(id);
        return enrollmentService.save(enrollment);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { enrollmentService.delete(id); }

    // Drop course (change status)
    @PutMapping("/{enrollmentId}/drop")
    public void dropCourse(@PathVariable Long enrollmentId) {
        enrollmentService.dropCourse(enrollmentId);
    }

    // Update status
    @PutMapping("/{enrollmentId}/status")
    public Enrollment updateStatus(@PathVariable Long enrollmentId, @RequestParam String status) {
        return enrollmentService.updateStatus(enrollmentId, status);
    }
}