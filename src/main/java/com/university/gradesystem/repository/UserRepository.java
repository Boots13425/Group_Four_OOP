package com.university.gradesystem.repository;

import com.university.gradesystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
//    List<User> findAllByRole(String role);
    Optional<User> findByUsernameAndPassword(String username, String password);
    boolean existsByEmail(String email);
}