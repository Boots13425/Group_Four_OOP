package com.university.gradesystem.service;

import com.university.gradesystem.model.Enrollment;
import com.university.gradesystem.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepo;

    public EnrollmentService(EnrollmentRepository enrollmentRepo) {
        this.enrollmentRepo = enrollmentRepo;
    }

    public List<Enrollment> getAll() { return enrollmentRepo.findAll(); }

    public Enrollment getById(Long id) { return enrollmentRepo.findById(id).orElse(null); }

    public Enrollment save(Enrollment enrollment) { return enrollmentRepo.save(enrollment); }

    public void delete(Long id) { enrollmentRepo.deleteById(id); }

    // Drop course (change status)
    public void dropCourse(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId).orElseThrow();
        enrollment.setStatus("DROPPED");
        enrollmentRepo.save(enrollment);
    }

    // Update status
    public Enrollment updateStatus(Long enrollmentId, String status) {
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId).orElseThrow();
        enrollment.setStatus(status);
        return enrollmentRepo.save(enrollment);
    }
}