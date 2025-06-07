package com.university.gradessystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "grade_policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "gradePolicy")
    private Course course;

    @ElementCollection
    @CollectionTable(name = "grade_scale", 
                    joinColumns = @JoinColumn(name = "policy_id"))
    @MapKeyColumn(name = "letter_grade")
    @Column(name = "min_percentage")
    private Map<String, Double> gradeScale = new HashMap<>();

    private boolean includePlusMinus = true;

    private boolean weightByCredits = true;

    private boolean includeTransfer = false;

    private LocalDate midtermDeadline;

    private LocalDate finalDeadline;

    private boolean automaticReminders = true;
}
