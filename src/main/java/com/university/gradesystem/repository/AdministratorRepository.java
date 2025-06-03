package com.university.gradesystem.repository;

import com.university.gradesystem.model.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    Optional<Administrator> findByAdminID(String adminID);
    List<Administrator> findAllByDepartment(String department);
    List<Administrator> findAllByFaculty(String faculty);
    List<Administrator> findByUsernameContainingIgnoreCase(String username);
}