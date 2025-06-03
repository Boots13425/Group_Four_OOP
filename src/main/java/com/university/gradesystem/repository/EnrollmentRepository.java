package com.university.gradesystem.repository;

import com.university.gradesystem.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findAllByStatus(String status);
    List<Enrollment> findAllByEnrollmentDate(LocalDate enrollmentDate);
    List<Enrollment> findByEnrollmentDateBetween(LocalDate start, LocalDate end);
}