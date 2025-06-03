package com.university.gradesystem.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {
    private Long enrollmentID;
    private LocalDate enrollmentDate;
    private String status;
}