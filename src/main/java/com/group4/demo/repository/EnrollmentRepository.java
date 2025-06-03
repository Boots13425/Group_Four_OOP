package com.group4.demo.repository;

import com.group4.demo.model.Enrollment;
import com.group4.demo.model.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Enrollment entity - handles enrollment-related database operations
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    /**
     * Find enrollments by student ID
     */
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId")
    List<Enrollment> findByStudentId(@Param("studentId") Long studentId);
    
    /**
     * Find active enrollments by student ID
     */
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.status = 'ACTIVE'")
    List<Enrollment> findActiveEnrollmentsByStudentId(@Param("studentId") Long studentId);
    
    /**
     * Find enrollments by course ID
     */
    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId")
    List<Enrollment> findByCourseId(@Param("courseId") Long courseId);
    
    /**
     * Find active enrollments by course ID
     */
    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId AND e.status = 'ACTIVE'")
    List<Enrollment> findActiveEnrollmentsByCourseId(@Param("courseId") Long courseId);
    
    /**
     * Check if student is already enrolled in a course
     */
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.course.id = :courseId AND e.status = 'ACTIVE'")
    Optional<Enrollment> findActiveEnrollment(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    
    /**
     * Find enrollments by term
     */
    List<Enrollment> findByTerm(String term);
    
    /**
     * Find enrollments by status
     */
    List<Enrollment> findByStatus(EnrollmentStatus status);
    
    /**
     * Count active enrollments for a course
     */
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.status = 'ACTIVE'")
    Long countActiveEnrollmentsByCourseId(@Param("courseId") Long courseId);
}
