package com.university.gradesystem.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorDTO {
    private Long userID;
    private String username;
    private String email;
    private String role;
    private String professorID;
    private String department;
    private String officeLocation;
    private String faculty;
}