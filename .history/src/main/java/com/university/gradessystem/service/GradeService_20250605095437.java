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

        // Set letter grade based on score
        if (grade.getScore() != null) {
            grade.setLetterGrade(convertToLetterGrade(grade.getScore()));
        }

        return gradeRepository.save(grade);
    }

    @Transactional
    public Grade updateGrade(Grade grade) {
        // Set letter grade based on score
        if (grade.getScore() != null) {
            grade.setLetterGrade(convertToLetterGrade(grade.getScore()));
        }

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

    // Updated letter grade conversion based on the specified grading policy
    public String convertToLetterGrade(Double score) {
        if (score == null)
            return "N/A";
        if (score >= 80)
            return "A";
        if (score >= 70)
            return "B+";
        if (score >= 60)
            return "B";
        if (score >= 55)
            return "C+";
        if (score >= 47)
            return "C";
        if (score >= 40)
            return "D";
        return "F";
    }

    private double convertToGPAScale(double percentage) {
        if (percentage >= 80)
            return 4.0; // A
        if (percentage >= 70)
            return 3.5; // B+
        if (percentage >= 60)
            return 3.0; // B
        if (percentage >= 55)
            return 2.5; // C+
        if (percentage >= 47)
            return 2.0; // C
        if (percentage >= 40)
            return 1.0; // D
        return 0.0; // F
    }
}
