package com.group4.demo.repository;

import com.group4.demo.model.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Administrator entity - handles admin-specific database operations
 */
@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    
    /**
     * Find administrator by username
     */
    Optional<Administrator> findByUsername(String username);
    
    /**
     * Find administrators by admin level
     */
    List<Administrator> findByAdminLevel(String adminLevel);
    
    /**
     * Find super administrators
     */
    @Query("SELECT a FROM Administrator a WHERE a.adminLevel = 'SUPER_ADMIN'")
    List<Administrator> findSuperAdministrators();
    
    /**
     * Check if admin has specific permission
     */
    @Query("SELECT a FROM Administrator a WHERE a.permissions LIKE CONCAT('%', :permission, '%')")
    List<Administrator> findByPermission(@Param("permission") String permission);
}
