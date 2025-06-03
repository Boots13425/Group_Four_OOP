package com.group4.demo.repository;

import com.group4.demo.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Professor entity - handles professor-specific database operations
 */
@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    
    /**
     * Find professor by employee ID
     */
    Optional<Professor> findByEmployeeId(String employeeId);
    
    /**
     * Find professor by username
     */
    Optional<Professor> findByUsername(String username);
    
    /**
     * Find professors by department
     */
    List<Professor> findByDepartment(String department);
    
    /**
     * Find professors who teach active courses
     */
    @Query("SELECT DISTINCT p FROM Professor p JOIN p.courses c WHERE c.isActive = true")
    List<Professor> findProfessorsWithActiveCourses();
    
    /**
     * Check if employee ID already exists
     */
    boolean existsByEmployeeId(String employeeId);
    
    /**
     * Find professors by name containing (case insensitive)
     */
    @Query("SELECT p FROM Professor p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Professor> findByNameContainingIgnoreCase(@Param("name") String name);
}
