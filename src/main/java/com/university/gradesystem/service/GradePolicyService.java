package com.university.gradesystem.service;

import com.university.gradesystem.model.Grade;
import com.university.gradesystem.model.GradePolicy;
import com.university.gradesystem.repository.GradePolicyRepository;
import com.university.gradesystem.util.GradeUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GradePolicyService {
    private final GradePolicyRepository policyRepo;

    public GradePolicyService(GradePolicyRepository policyRepo) {
        this.policyRepo = policyRepo;
    }

    public List<GradePolicy> getAll() { return policyRepo.findAll(); }

    public Optional<GradePolicy> getById(Long id) { return policyRepo.findById(id); }

    public GradePolicy save(GradePolicy policy) { return policyRepo.save(policy); }

    public void delete(Long id) { policyRepo.deleteById(id); }

    // --- Custom ---

    // Update policy
    public GradePolicy updatePolicy(Long policyId, String gradingScale, String passingGrade, String lateSubmissionPolicy) {
        GradePolicy policy = policyRepo.findById(policyId).orElseThrow();
        policy.setGradingScale(gradingScale);
        policy.setPassingGrade(passingGrade);
        policy.setLateSubmissionPolicy(lateSubmissionPolicy);
        return policyRepo.save(policy);
    }

    // Apply policy to grade (assigns letter grade)
    public String applyPolicy(Grade grade, GradePolicy policy) {
        String letter = GradeUtils.computeLetterGrade(grade.getValue(), policy.getGradingScale());
        grade.setLetterGrade(letter);
        return letter;
    }

    // Validate a grade against policy
    public boolean validateGrade(Grade grade, GradePolicy policy) {
        return GradeUtils.isPassing(grade, policy);
    }
}