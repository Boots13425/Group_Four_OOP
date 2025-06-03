package com.group4.demo.repository;

import com.group4.demo.model.User;
import com.group4.demo.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity - handles database operations for all user types
 * This is the data access layer that talks directly to the database
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username - used for login authentication
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find user by email - useful for password reset functionality
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find users by role - useful for admin operations
     */
    List<User> findByRole(UserRole role);
    
    /**
     * Find active users only
     */
    List<User> findByIsActiveTrue();
    
    /**
     * Check if username already exists - for registration validation
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if email already exists - for registration validation
     */
    boolean existsByEmail(String email);
    
    /**
     * Find users by name containing (case insensitive) - for search functionality
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContainingIgnoreCase(@Param("name") String name);
}
