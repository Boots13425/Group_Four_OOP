package com.university.gradessystem.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "grade_policies")
public class GradePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "gradePolicy")
    private Course course;

    @ElementCollection
    @CollectionTable(name = "grade_scale", joinColumns = @JoinColumn(name = "policy_id"))
    @MapKeyColumn(name = "letter_grade")
    @Column(name = "min_percentage")
    private Map<String, Double> gradeScale = new HashMap<>();

    private boolean includePlusMinus = true;

    private boolean weightByCredits = true;

    private boolean includeTransfer = false;

    private LocalDate midtermDeadline;

    private LocalDate finalDeadline;

    private boolean automaticReminders = true;

    // Constructors
    public GradePolicy() {
    }

    public GradePolicy(Long id, Course course, Map<String, Double> gradeScale, boolean includePlusMinus,
            boolean weightByCredits, boolean includeTransfer, LocalDate midtermDeadline,
            LocalDate finalDeadline, boolean automaticReminders) {
        this.id = id;
        this.course = course;
        this.gradeScale = gradeScale;
        this.includePlusMinus = includePlusMinus;
        this.weightByCredits = weightByCredits;
        this.includeTransfer = includeTransfer;
        this.midtermDeadline = midtermDeadline;
        this.finalDeadline = finalDeadline;
        this.automaticReminders = automaticReminders;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Map<String, Double> getGradeScale() {
        return gradeScale;
    }

    public void setGradeScale(Map<String, Double> gradeScale) {
        this.gradeScale = gradeScale;
    }

    public boolean isIncludePlusMinus() {
        return includePlusMinus;
    }

    public void setIncludePlusMinus(boolean includePlusMinus) {
        this.includePlusMinus = includePlusMinus;
    }

    public boolean isWeightByCredits() {
        return weightByCredits;
    }

    public void setWeightByCredits(boolean weightByCredits) {
        this.weightByCredits = weightByCredits;
    }

    public boolean isIncludeTransfer() {
        return includeTransfer;
    }

    public void setIncludeTransfer(boolean includeTransfer) {
        this.includeTransfer = includeTransfer;
    }

    public LocalDate getMidtermDeadline() {
        return midtermDeadline;
    }

    public void setMidtermDeadline(LocalDate midtermDeadline) {
        this.midtermDeadline = midtermDeadline;
    }

    public LocalDate getFinalDeadline() {
        return finalDeadline;
    }

    public void setFinalDeadline(LocalDate finalDeadline) {
        this.finalDeadline = finalDeadline;
    }

    public boolean isAutomaticReminders() {
        return automaticReminders;
    }

    public void setAutomaticReminders(boolean automaticReminders) {
        this.automaticReminders = automaticReminders;
    }
}
