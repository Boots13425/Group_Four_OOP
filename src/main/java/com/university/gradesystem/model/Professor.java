package com.university.gradesystem.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("PROFESSOR")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Professor extends User {
    private String professorID;
    private String department;
    private String office;
    private String faculty;

    @Transient
    public String getRole() {
        return "PROFESSOR";
    }
}