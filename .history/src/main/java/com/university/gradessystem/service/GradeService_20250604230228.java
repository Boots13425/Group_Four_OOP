package com.university.gradessystem.service;

import com.university.gradessystem.model.Course;
import com.university.gradessystem.model.Enrollment;
import com.university.gradessystem.model.Grade;
import com.university.gradessystem.model.User;
import com.university.gradessystem.repository.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GradeService {

    private final GradeRepository gradeRepository;
    private EnrollmentService enrollmentService;

    public GradeService(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    @Autowired
    @Lazy
    public void setEnrollmentService(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    public Optional<Grade> getGradeById(Long id) {
        return gradeRepository.findById(id);
    }

    public List<Grade> getGradesByEnrollment(Enrollment enrollment) {
        return gradeRepository.findByEnrollment(enrollment);
    }

    public List<Grade> getGradesByEnrollmentAndType(Enrollment enrollment, String assignmentType) {
        return gradeRepository.findByEnrollmentAndAssignmentType(enrollment, assignmentType);
    }

    @Transactional
    public Grade addGrade(Grade grade) {
        if (grade.getGradedDate() == null) {
            grade.setGradedDate(LocalDateTime.now());
        }
        return gradeRepository.save(grade);
    }

    @Transactional
    public Grade updateGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    @Transactional
    public void deleteGrade(Long id) {
        gradeRepository.deleteById(id);
    }

    public Double calculateAverageGrade(Enrollment enrollment) {
        return gradeRepository.calculateAverageGrade(enrollment);
    }

    public Double calculateAverageGradeForCourse(Long courseId) {
        return gradeRepository.calculateAverageGradeForCourse(courseId);
    }

    public List<Grade> getRecentGradesByStudentId(Long studentId) {
        return gradeRepository.findRecentGradesByStudentId(studentId);
    }

    public Double calculateGPA(User student) {
        if (enrollmentService == null) {
            return 0.0;
        }

        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(student);

        if (enrollments.isEmpty()) {
            return 0.0;
        }

        double totalPoints = 0.0;
        int totalCredits = 0;

        for (Enrollment enrollment : enrollments) {
            Course course = enrollment.getCourse();
            Double avgGrade = calculateAverageGrade(enrollment);

            if (avgGrade != null) {
                // Convert percentage to 4.0 scale
                double gpaPoints = convertToGPAScale(avgGrade);
                totalPoints += gpaPoints * course.getCredits();
                totalCredits += course.getCredits();
            }
        }

        return totalCredits > 0 ? totalPoints / totalCredits : 0.0;
    }

    private double convertToGPAScale(double percentage) {
        if (percentage >= 90)
            return 4.0;
        if (percentage >= 80)
            return 3.0;
        if (percentage >= 70)
            return 2.0;
        if (percentage >= 60)
            return 1.0;
        return 0.0;
    }
}
