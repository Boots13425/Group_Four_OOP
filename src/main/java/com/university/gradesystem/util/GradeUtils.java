package com.university.gradesystem.util;

import com.university.gradesystem.model.Grade;
import com.university.gradesystem.model.GradePolicy;
import java.util.HashMap;
import java.util.Map;

public class GradeUtils {
    public static Map<String, int[]> parseGradingScale(String gradingScale) {
        Map<String, int[]> scaleMap = new HashMap<>();
        if (gradingScale == null || gradingScale.isEmpty()) return scaleMap;
        String[] grades = gradingScale.split(";");
        for (String grade : grades) {
            String[] parts = grade.split(":");
            if (parts.length == 2) {
                String letter = parts[0].trim();
                String[] range = parts[1].split("-");
                if (range.length == 2) {
                    int min = Integer.parseInt(range[0].trim());
                    int max = Integer.parseInt(range[1].trim());
                    scaleMap.put(letter, new int[]{min, max});
                }
            }
        }
        return scaleMap;
    }

    public static String computeLetterGrade(double value, String gradingScale) {
        Map<String, int[]> scaleMap = parseGradingScale(gradingScale);
        for (Map.Entry<String, int[]> entry : scaleMap.entrySet()) {
            int min = entry.getValue()[0];
            int max = entry.getValue()[1];
            if (value >= min && value <= max) {
                return entry.getKey();
            }
        }
        return "F";
    }

    public static boolean isPassing(Grade grade, GradePolicy policy) {
        String passingGrade = policy.getPassingGrade();
        Map<String, int[]> scaleMap = parseGradingScale(policy.getGradingScale());
        return grade.getLetterGrade().compareToIgnoreCase(passingGrade) <= 0 && scaleMap.containsKey(grade.getLetterGrade());
    }

    public static double applyLatePenalty(double score, int daysLate, String latePolicy) {
        if (latePolicy == null || !latePolicy.contains("%")) return score;
        String[] parts = latePolicy.split("%");
        try {
            double percent = Double.parseDouble(parts[0].trim());
            double penalty = percent / 100 * daysLate * score;
            return Math.max(0, score - penalty);
        } catch (Exception e) {
            return score;
        }
    }
}