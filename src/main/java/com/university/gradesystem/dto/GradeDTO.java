package com.university.gradesystem.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeDTO {
    private Long gradeID;
    private double value;
    private String letterGrade;
    private LocalDateTime timestamp;
    private boolean isFinalized;
}