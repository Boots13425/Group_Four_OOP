package com.university.gradesystem.repository;

import com.university.gradesystem.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByProfessorID(String professorID);
    List<Professor> findAllByDepartment(String department);
    List<Professor> findAllByFaculty(String faculty);
    List<Professor> findByUsernameContainingIgnoreCase(String username);
}