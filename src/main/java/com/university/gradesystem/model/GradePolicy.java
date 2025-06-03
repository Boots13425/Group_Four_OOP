package com.university.gradesystem.model;

import jakarta.persistence.*;

@Entity
public class GradePolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long policyID;

    private String name;
    private String description;
    private String rules;
    private String gradingScale;
    private String passingGrade;
    private String lateSubmissionPolicy; // <-- Add this field

    public GradePolicy() {}

    public Long getPolicyID() {
        return policyID;
    }

    public void setPolicyID(Long policyID) {
        this.policyID = policyID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getGradingScale() {
        return gradingScale;
    }

    public void setGradingScale(String gradingScale) {
        this.gradingScale = gradingScale;
    }

    public String getPassingGrade() {
        return passingGrade;
    }

    public void setPassingGrade(String passingGrade) {
        this.passingGrade = passingGrade;
    }

    public String getLateSubmissionPolicy() {
        return lateSubmissionPolicy;
    }

    public void setLateSubmissionPolicy(String lateSubmissionPolicy) {
        this.lateSubmissionPolicy = lateSubmissionPolicy;
    }
}