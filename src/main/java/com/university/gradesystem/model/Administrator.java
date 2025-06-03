package com.university.gradesystem.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("ADMIN")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Administrator extends User {
    private String adminID;
    private String department;
    private String faculty;

    /**
     * Expose the role for business logic or API responses if needed.
     * This is not persisted in the DB; it's for convenience.
     */
    @Transient
    public String getRole() {
        return "ADMIN";
    }
}