package com.university.gradesystem.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long courseID;
    private String courseName;
    private int credits;
    private String semester;
    private int maxCapacity;
    private int currentEnrollment;
}