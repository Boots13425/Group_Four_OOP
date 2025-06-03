package com.university.gradesystem.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("STUDENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true) // Fixes Lombok warning for inheritance
public class Student extends User {
    private String studentID;
    private String major;
    private String year;
    private int enrollmentYear;
    private double currentGPA;

    @Transient
    public String getRole() {
        return "STUDENT";
    }
}