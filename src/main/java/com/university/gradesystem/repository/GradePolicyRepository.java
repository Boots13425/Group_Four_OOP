package com.university.gradesystem.repository;

import com.university.gradesystem.model.GradePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GradePolicyRepository extends JpaRepository<GradePolicy, Long> {
    List<GradePolicy> findAllByGradingScale(String gradingScale);
    Optional<GradePolicy> findByPassingGrade(String passingGrade);
}