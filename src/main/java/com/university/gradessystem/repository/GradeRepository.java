package com.university.gradessystem.repository;

import com.university.gradessystem.model.Enrollment;
import com.university.gradessystem.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    
    List<Grade> findByEnrollment(Enrollment enrollment);
    
    List<Grade> findByEnrollmentAndAssignmentType(Enrollment enrollment, String assignmentType);
    
    @Query("SELECT AVG(g.score) FROM Grade g WHERE g.enrollment = :enrollment")
    Double calculateAverageGrade(@Param("enrollment") Enrollment enrollment);
    
    @Query("SELECT AVG(g.score) FROM Grade g JOIN g.enrollment e WHERE e.course.id = :courseId")
    Double calculateAverageGradeForCourse(@Param("courseId") Long courseId);
    
    @Query("SELECT g FROM Grade g JOIN g.enrollment e WHERE e.student.id = :studentId ORDER BY g.gradedDate DESC")
    List<Grade> findRecentGradesByStudentId(@Param("studentId") Long studentId);
}
