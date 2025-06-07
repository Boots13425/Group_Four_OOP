package com.university.gradessystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String courseCode;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private Integer credits;

    @Column(nullable = false)
    private String department;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private User professor;

    private Integer capacity;

    private boolean active = true;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Enrollment> enrollments = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "grade_policy_id")
    private GradePolicy gradePolicy;

    private String semester;
    private String academicYear;
}
