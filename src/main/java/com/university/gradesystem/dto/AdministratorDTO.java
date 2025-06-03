package com.university.gradesystem.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdministratorDTO {
    private Long userID;
    private String username;
    private String email;
    private String role;
    private String adminID;
    private String department;
    private String faculty;
}