package com.university.gradessystem.repository;

import com.university.gradessystem.model.GradePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradePolicyRepository extends JpaRepository<GradePolicy, Long> {
}
