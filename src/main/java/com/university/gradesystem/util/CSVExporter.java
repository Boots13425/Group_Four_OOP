package com.university.gradesystem.util;

import com.university.gradesystem.model.Grade;

import java.util.List;

public class CSVExporter {
    public static String gradesToCSV(List<Grade> grades) {
        StringBuilder sb = new StringBuilder("Course,Score,LetterGrade,Timestamp,IsFinalized\n");
        for (Grade grade : grades) {
            sb.append(grade.getEnrollment() != null && grade.getEnrollment().getCourse() != null
                    ? grade.getEnrollment().getCourse().getCourseName() : "N/A").append(",");
            sb.append(grade.getValue()).append(",");
            sb.append(grade.getLetterGrade()).append(",");
            sb.append(grade.getTimestamp()).append(",");
            sb.append(grade.isFinalized()).append("\n");
        }
        return sb.toString();
    }
}