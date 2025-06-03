package com.university.gradesystem.repository;

import com.university.gradesystem.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDateTime;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findAllByLetterGrade(String letterGrade);
    List<Grade> findAllByIsFinalized(boolean isFinalized);
    List<Grade> findAllByValueGreaterThanEqual(double value);
    List<Grade> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}