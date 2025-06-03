package com.university.gradesystem.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Long userID;
    private String username;
    private String email;
    private String role;
    private String studentID;
    private String major;
    private int enrollmentYear;
    private double currentGPA;
}