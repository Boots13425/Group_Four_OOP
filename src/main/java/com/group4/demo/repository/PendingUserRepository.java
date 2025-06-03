package com.group4.demo.repository;

import com.group4.demo.model.PendingUser;
import com.group4.demo.service.UserRegistrationService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for PendingUser entity - handles pending user registration operations
 */
@Repository
public interface PendingUserRepository extends JpaRepository<PendingUser, Long> {
    
    /**
     * Find pending user by identifier (matricule or email)
     */
    Optional<PendingUser> findByIdentifier(String identifier);
    
    /**
     * Find pending user by identifier and verification code
     */
    Optional<PendingUser> findByIdentifierAndVerificationCode(String identifier, String verificationCode);
    
    /**
     * Check if identifier already exists in pending users
     */
    boolean existsByIdentifier(String identifier);
    
    /**
     * Find pending users by user type
     */
    List<PendingUser> findByUserType(UserRegistrationService.UserType userType);
    
    /**
     * Find expired pending users (older than 24 hours)
     */
    @Query("SELECT p FROM PendingUser p WHERE p.createdAt < :expirationTime")
    List<PendingUser> findExpiredPendingUsers(@Param("expirationTime") LocalDateTime expirationTime);
    
    /**
     * Delete expired pending users
     */
    @Query("DELETE FROM PendingUser p WHERE p.createdAt < :expirationTime")
    void deleteExpiredPendingUsers(@Param("expirationTime") LocalDateTime expirationTime);
    
    /**
     * Find pending users by email
     */
    Optional<PendingUser> findByEmail(String email);
    
    /**
     * Find pending users created after a specific date
     */
    List<PendingUser> findByCreatedAtAfter(LocalDateTime date);
}
