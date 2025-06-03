package com.group4.demo.repository;

import com.group4.demo.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Grade entity - handles grade-related database operations
 */
@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    /**
     * Find grades by student ID
     */
    @Query ("SELECT g FROM Grade g WHERE g.student.id = :studentId ORDER BY g.gradeDate DESC")
    List<Grade> findByStudentId(@Param("studentId") Long studentId);

    /**
     * Find grades by student ID and term
     */
    List<Grade> findByStudentIdAndTerm(@Param("studentId") Long studentId, @Param("term") String term);
    
    /**
     * Find grades by course ID
     */
    List<Grade> findByCourseId(@Param("courseId") Long courseId);
    
    /**
     * Find grade for specific student and course
     */
    Optional<Grade> findByStudentIdAndCourseIdAndTerm(@Param("studentId") Long studentId,
                                                      @Param("courseId") Long courseId, 
                                                      @Param("term") String term);
    
    /**
     * Find all terms for a student (for historical grades)
     */
    List<String> findDistinctTermsByStudentId(@Param("studentId") Long studentId);
    
    /**
     * Find grades by letter grade
     */
    List<Grade> findByLetterGrade(String letterGrade);
    
    /**
     * Calculate average GPA for a course
     */
    Double calculateAverageGPAForCourse(@Param("courseId") Long courseId);
    
    /**
     * Find failing grades (F grades)
     */
    List<Grade> findFailingGrades();
}
