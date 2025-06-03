package com.group4.demo.repository;

import com.group4.demo.model.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for SystemConfig entity - handles system configuration operations
 */
@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
    
    /**
     * Get the current system configuration (should only be one record)
     */
    @Query("SELECT s FROM SystemConfig s ORDER BY s.id DESC")
    Optional<SystemConfig> findCurrentConfig();
    
    /**
     * Check if any configuration exists
     */
    boolean existsBySystemName(String systemName);
}
