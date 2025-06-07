package com.university.gradessystem.repository;

import com.university.gradessystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    List<User> findByRole(User.Role role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'ROLE_STUDENT'")
    long countStudents();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'ROLE_PROFESSOR'")
    long countProfessors();
    
    List<User> findByRoleAndActive(User.Role role, boolean active);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}
