package com.group4.demo.repository;

import com.group4.demo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Student entity - handles student-specific database operations
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    /**
     * Find student by student ID
     */
    Optional<Student> findByStudentId(String studentId);
    
    /**
     * Find student by username
     */
    Optional<Student> findByUsername(String username);
    
    /**
     * Find students with GPA above a certain threshold
     */
    @Query("SELECT s FROM Student s WHERE s.currentGPA >= :minGpa")
    List<Student> findByCurrentGPAGreaterThanEqual(@Param("minGpa") Double minGpa);
    
    /**
     * Find students enrolled in a specific course
     */
    @Query("SELECT DISTINCT s FROM Student s JOIN s.enrollments e WHERE e.course.id = :courseId AND e.status = 'ACTIVE'")
    List<Student> findByCourseId(@Param("courseId") Long courseId);
    
    /**
     * Check if student ID already exists
     */
    boolean existsByStudentId(String studentId);
    
    /**
     * Find top performing students (by GPA)
     */
    @Query("SELECT s FROM Student s WHERE s.currentGPA > 0 ORDER BY s.currentGPA DESC")
    List<Student> findTopPerformers();
}
