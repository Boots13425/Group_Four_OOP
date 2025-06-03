package com.university.gradesystem.controller;

import com.university.gradesystem.model.Grade;
import com.university.gradesystem.model.GradePolicy;
import com.university.gradesystem.service.GradePolicyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gradepolicies")
public class GradePolicyController {
    private final GradePolicyService gradePolicyService;
    public GradePolicyController(GradePolicyService gradePolicyService) { this.gradePolicyService = gradePolicyService; }

    @GetMapping
    public List<GradePolicy> getAll() { return gradePolicyService.getAll(); }

    @GetMapping("/{id}")
    public GradePolicy getById(@PathVariable Long id) { return gradePolicyService.getById(id).orElse(null); }

    @PostMapping
    public GradePolicy create(@RequestBody GradePolicy policy) { return gradePolicyService.save(policy); }

    @PutMapping("/{id}")
    public GradePolicy update(@PathVariable Long id, @RequestBody GradePolicy policy) {
        policy.setPolicyID(id);
        return gradePolicyService.save(policy);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { gradePolicyService.delete(id); }

    // Update grading policy
    @PutMapping("/{policyId}/update-policy")
    public GradePolicy updatePolicy(@PathVariable Long policyId, @RequestParam String gradingScale,
                                    @RequestParam String passingGrade, @RequestParam String lateSubmissionPolicy) {
        return gradePolicyService.updatePolicy(policyId, gradingScale, passingGrade, lateSubmissionPolicy);
    }

    // Apply policy to a grade
    @PostMapping("/{policyId}/apply-policy")
    public String applyPolicy(@PathVariable Long policyId, @RequestBody Grade grade) {
        GradePolicy policy = gradePolicyService.getById(policyId).orElse(null);
        return policy != null ? gradePolicyService.applyPolicy(grade, policy) : "Policy not found";
    }

    // Validate a grade against policy
    @PostMapping("/{policyId}/validate-grade")
    public boolean validateGrade(@PathVariable Long policyId, @RequestBody Grade grade) {
        GradePolicy policy = gradePolicyService.getById(policyId).orElse(null);
        return policy != null && gradePolicyService.validateGrade(grade, policy);
    }
}