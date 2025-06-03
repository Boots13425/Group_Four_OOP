package com.university.gradesystem.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userID;
    private String username;
    private String email;
    private String role;
}