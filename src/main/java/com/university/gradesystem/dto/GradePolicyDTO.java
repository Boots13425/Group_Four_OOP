package com.university.gradesystem.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradePolicyDTO {
    private Long policyID;
    private String gradingScale;
    private String passingGrade;
    private String lateSubmissionPolicy;
}