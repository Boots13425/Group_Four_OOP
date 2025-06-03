package com.university.gradesystem.service;

import com.university.gradesystem.model.Grade;
import com.university.gradesystem.repository.GradeRepository;
import com.university.gradesystem.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeService {
    private final GradeRepository gradeRepo;
    private final EnrollmentRepository enrollmentRepo;

    public GradeService(GradeRepository gradeRepo, EnrollmentRepository enrollmentRepo) {
        this.gradeRepo = gradeRepo;
        this.enrollmentRepo = enrollmentRepo;
    }

    public List<Grade> getAll() { return gradeRepo.findAll(); }

    public Grade getById(Long id) { return gradeRepo.findById(id).orElse(null); }

    public Grade save(Grade grade) { return gradeRepo.save(grade); }

    public void delete(Long id) { gradeRepo.deleteById(id); }

    // --- Custom ---

    // Calculate GPA for a student
    public double calculateGPA(Long studentId) {
        return enrollmentRepo.findAll().stream()
                .filter(e -> e.getStudent().getUserID().equals(studentId))
                .flatMap(e -> gradeRepo.findAll().stream().filter(g -> g.getEnrollment().getEnrollmentID().equals(e.getEnrollmentID())))
                .mapToDouble(Grade::getValue)
                .average()
                .orElse(0.0);
    }

    // Update a grade
    public Grade updateGrade(Long gradeId, double value, String letter, boolean isFinalized) {
        Grade grade = gradeRepo.findById(gradeId).orElseThrow();
        grade.setValue(value);
        grade.setLetterGrade(letter);
        grade.setFinalized(isFinalized);
        grade.setTimestamp(java.time.LocalDateTime.now());
        return gradeRepo.save(grade);
    }

    // Finalize a grade
    public Grade finalizeGrade(Long gradeId) {
        Grade grade = gradeRepo.findById(gradeId).orElseThrow();
        grade.setFinalized(true);
        return gradeRepo.save(grade);
    }
}