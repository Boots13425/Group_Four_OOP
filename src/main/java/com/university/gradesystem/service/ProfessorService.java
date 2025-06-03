package com.university.gradesystem.service;

import com.university.gradesystem.model.Enrollment;
import com.university.gradesystem.model.Grade;
import com.university.gradesystem.model.Professor;
import com.university.gradesystem.repository.EnrollmentRepository;
import com.university.gradesystem.repository.GradeRepository;
import com.university.gradesystem.repository.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfessorService {
    private final ProfessorRepository professorRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final GradeRepository gradeRepo;

    public ProfessorService(ProfessorRepository professorRepo, EnrollmentRepository enrollmentRepo, GradeRepository gradeRepo) {
        this.professorRepo = professorRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.gradeRepo = gradeRepo;
    }

    public List<Professor> getAll() { return professorRepo.findAll(); }

    public Optional<Professor> getById(Long id) { return professorRepo.findById(id); }

    public Professor save(Professor professor) { return professorRepo.save(professor); }

    public void delete(Long id) { professorRepo.deleteById(id); }

    // --- Custom methods ---

    // Upload marks for a student's enrollment
    public Grade uploadMarks(Long enrollmentId, double value, String letterGrade) {
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId).orElseThrow();
        Grade grade = new Grade();
        grade.setEnrollment(enrollment);
        grade.setValue(value);
        grade.setLetterGrade(letterGrade);
        grade.setTimestamp(java.time.LocalDateTime.now());
        grade.setFinalized(false);
        return gradeRepo.save(grade);
    }

    // Update marks
    public Grade updateMarks(Long gradeId, double value, String letterGrade) {
        Grade grade = gradeRepo.findById(gradeId).orElseThrow();
        grade.setValue(value);
        grade.setLetterGrade(letterGrade);
        grade.setTimestamp(java.time.LocalDateTime.now());
        return gradeRepo.save(grade);
    }

    // View all grades for a course
    public List<Grade> viewCourseGrades(Long courseId) {
        return enrollmentRepo.findAll().stream()
                .filter(e -> e.getCourse().getCourseID().equals(courseId))
                .flatMap(e -> gradeRepo.findAll().stream().filter(g -> g.getEnrollment().getEnrollmentID().equals(e.getEnrollmentID())))
                .collect(Collectors.toList());
    }
}