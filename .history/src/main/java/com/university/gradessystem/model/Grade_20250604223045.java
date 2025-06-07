package com.university.gradessystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @Column(nullable = false)
    private String assignmentType; // exam, assignment, quiz, project

    @Column(nullable = false)
    private String assignmentName;

    private Double score;

    private String letterGrade;

    private Double weightPercentage;

    private LocalDateTime submissionDate;

    private LocalDateTime gradedDate;

    @ManyToOne
    @JoinColumn(name = "graded_by")
    private User gradedBy;

    private String comments;

    // Constructors
    public Grade() {
    }

    public Grade(Long id, Enrollment enrollment, String assignmentType, String assignmentName,
            Double score, String letterGrade, Double weightPercentage, LocalDateTime submissionDate,
            LocalDateTime gradedDate, User gradedBy, String comments) {
        this.id = id;
        this.enrollment = enrollment;
        this.assignmentType = assignmentType;
        this.assignmentName = assignmentName;
        this.score = score;
        this.letterGrade = letterGrade;
        this.weightPercentage = weightPercentage;
        this.submissionDate = submissionDate;
        this.gradedDate = gradedDate;
        this.gradedBy = gradedBy;
        this.comments = comments;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public String getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(String assignmentType) {
        this.assignmentType = assignmentType;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getLetterGrade() {
        return letterGrade;
    }

    public void setLetterGrade(String letterGrade) {
        this.letterGrade = letterGrade;
    }

    public Double getWeightPercentage() {
        return weightPercentage;
    }

    public void setWeightPercentage(Double weightPercentage) {
        this.weightPercentage = weightPercentage;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public LocalDateTime getGradedDate() {
        return gradedDate;
    }

    public void setGradedDate(LocalDateTime gradedDate) {
        this.gradedDate = gradedDate;
    }

    public User getGradedBy() {
        return gradedBy;
    }

    public void setGradedBy(User gradedBy) {
        this.gradedBy = gradedBy;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
