package com.group4.demo.repository;

import com.group4.demo.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Course entity - handles course-related database operations
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    /**
     * Find course by course code
     */
    Optional<Course> findByCourseCode(String courseCode);
    
    /**
     * Find all active courses
     */
    List<Course> findByIsActiveTrue();
    
    /**
     * Find courses by professor ID
     */
    @Query("SELECT c FROM Course c WHERE c.professor.id = :professorId AND c.isActive = true")
    List<Course> findByProfessorId(@Param("professorId") Long professorId);
    
    /**
     * Find courses with available slots for enrollment
     */
    @Query("SELECT c FROM Course c WHERE c.currentEnrollment < c.capacity AND c.isActive = true")
    List<Course> findAvailableCoursesForEnrollment();
    
    /**
     * Find courses by title containing (case insensitive)
     */
    @Query("SELECT c FROM Course c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%')) AND c.isActive = true")
    List<Course> findByTitleContainingIgnoreCase(@Param("title") String title);
    
    /**
     * Check if course code already exists
     */
    boolean existsByCourseCode(String courseCode);
    
    /**
     * Find courses by credit hours
     */
    List<Course> findByCredits(Integer credits);
}
