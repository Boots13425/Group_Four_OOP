package com.university.gradessystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
