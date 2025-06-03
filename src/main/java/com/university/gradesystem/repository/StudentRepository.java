package com.university.gradesystem.repository;

import com.university.gradesystem.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentID(String studentID);
    List<Student> findAllByMajor(String major);
    List<Student> findByCurrentGPAGreaterThanEqual(double gpa);
    List<Student> findByUsernameContainingIgnoreCase(String username);
    List<Student> findAllByEnrollmentYear(int enrollmentYear);
}