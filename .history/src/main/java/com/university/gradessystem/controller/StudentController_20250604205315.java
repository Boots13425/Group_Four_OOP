package com.university.gradessystem.controller;

import com.university.gradessystem.model.*;
import com.university.gradessystem.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final UserService userService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final GradeService gradeService;
    private final NotificationService notificationService;

    @GetMapping
    public String studentDashboard(Authentication authentication, Model model) {
        User student = (User) authentication.getPrincipal();
        
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(student);
        long enrolledCourses = enrollments.stream()
                .filter(e -> e.getStatus() == Enrollment.EnrollmentStatus.ENROLLED)
                .count();
        
        Double gpa = gradeService.calculateGPA(student);
        
        // Calculate average grade
        double totalGrade = 0;
        int gradeCount = 0;
        for (Enrollment enrollment : enrollments) {
            Double avgGrade = gradeService.calculateAverageGrade(enrollment);
            if (avgGrade != null) {
                totalGrade += avgGrade;
                gradeCount++;
            }
        }
        double averageGrade = gradeCount > 0 ? totalGrade / gradeCount : 0;
        
        model.addAttribute("enrolledCourses", enrolledCourses);
        model.addAttribute("currentGpa", String.format("%.2f", gpa));
        model.addAttribute("averageGrade", String.format("%.1f%%", averageGrade));
        
        // Recent grades
        List<Grade> recentGrades = gradeService.getRecentGradesByStudentId(student.getId());
        model.addAttribute("recentGrades", recentGrades.stream().limit(5).collect(Collectors.toList()));
        
        return "student";
    }

    @GetMapping("/api/courses/available")
    @ResponseBody
    public List<Map<String, Object>> getAvailableCourses(Authentication authentication) {
        User student = (User) authentication.getPrincipal();
        List<Course> allCourses = courseService.getActiveCourses();
        List<Enrollment> studentEnrollments = enrollmentService.getEnrollmentsByStudent(student);
        
        // Get course IDs student is already enrolled in
        List<Long> enrolledCourseIds = studentEnrollments.stream()
                .map(e -> e.getCourse().getId())
                .collect(Collectors.toList());
        
        return allCourses.stream()
                .filter(course -> !enrolledCourseIds.contains(course.getId()))
                .map(course -> {
                    Map<String, Object> courseInfo = new HashMap<>();
                    courseInfo.put("id", course.getId());
                    courseInfo.put("courseCode", course.getCourseCode());
                    courseInfo.put("title", course.getTitle());
                    courseInfo.put("description", course.getDescription());
                    courseInfo.put("credits", course.getCredits());
                    courseInfo.put("department", course.getDepartment());
                    courseInfo.put("professor", course.getProfessor().getFullName());
                    courseInfo.put("capacity", course.getCapacity());
                    courseInfo.put("enrolled", courseService.countEnrolledStudents(course));
                    return courseInfo;
                })
                .collect(Collectors.toList());
    }

    @PostMapping("/api/courses/{courseId}/enroll")
    @ResponseBody
    public ResponseEntity<?> enrollInCourse(@PathVariable Long courseId, Authentication authentication) {
        User student = (User) authentication.getPrincipal();
        
        try {
            Enrollment enrollment = enrollmentService.enrollStudent(student.getId(), courseId);
            return ResponseEntity.ok(enrollment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/api/enrollments")
    @ResponseBody
    public List<Map<String, Object>> getStudentEnrollments(Authentication authentication) {
        User student = (User) authentication.getPrincipal();
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(student);
        
        return enrollments.stream()
                .map(enrollment -> {
                    Map<String, Object> enrollmentInfo = new HashMap<>();
                    enrollmentInfo.put("id", enrollment.getId());
                    enrollmentInfo.put("course", enrollment.getCourse().getTitle());
                    enrollmentInfo.put("courseCode", enrollment.getCourse().getCourseCode());
                    enrollmentInfo.put("credits", enrollment.getCourse().getCredits());
                    enrollmentInfo.put("status", enrollment.getStatus());
                    enrollmentInfo.put("enrollmentDate", enrollment.getEnrollmentDate());
                    
                    // Calculate grade
                    Double avgGrade = gradeService.calculateAverageGrade(enrollment);
                    enrollmentInfo.put("grade", avgGrade);
                    
                    // Convert to letter grade
                    String letterGrade = convertToLetterGrade(avgGrade);
                    enrollmentInfo.put("letterGrade", letterGrade);
                    
                    return enrollmentInfo;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/api/grades")
    @ResponseBody
    public List<Map<String, Object>> getStudentGrades(Authentication authentication) {
        User student = (User) authentication.getPrincipal();
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(student);
        
        List<Map<String, Object>> allGrades = new ArrayList<>();
        
        for (Enrollment enrollment : enrollments) {
            List<Grade> grades = gradeService.getGradesByEnrollment(enrollment);
            
            for (Grade grade : grades) {
                Map<String, Object> gradeInfo = new HashMap<>();
                gradeInfo.put("id", grade.getId());
                gradeInfo.put("course", enrollment.getCourse().getTitle());
                gradeInfo.put("courseCode", enrollment.getCourse().getCourseCode());
                gradeInfo.put("assignmentType", grade.getAssignmentType());
                gradeInfo.put("assignmentName", grade.getAssignmentName());
                gradeInfo.put("score", grade.getScore());
                gradeInfo.put("letterGrade", convertToLetterGrade(grade.getScore()));
                gradeInfo.put("gradedDate", grade.getGradedDate());
                gradeInfo.put("comments", grade.getComments());
                
                allGrades.add(gradeInfo);
            }
        }
        
        return allGrades;
    }

    @GetMapping("/api/gpa")
    @ResponseBody
    public Map<String, Object> getStudentGPA(Authentication authentication) {
        User student = (User) authentication.getPrincipal();
        Double gpa = gradeService.calculateGPA(student);
        
        Map<String, Object> result = new HashMap<>();
        result.put("gpa", gpa);
        result.put("studentName", student.getFullName());
        
        // Get semester breakdown
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(student);
        List<Map<String, Object>> breakdown = new ArrayList<>();
        
        for (Enrollment enrollment : enrollments) {
            Map<String, Object> courseGrade = new HashMap<>();
            courseGrade.put("course", enrollment.getCourse().getTitle());
            courseGrade.put("courseCode", enrollment.getCourse().getCourseCode());
            courseGrade.put("credits", enrollment.getCourse().getCredits());
            
            Double avgGrade = gradeService.calculateAverageGrade(enrollment);
            courseGrade.put("average", avgGrade);
            courseGrade.put("letterGrade", convertToLetterGrade(avgGrade));
            courseGrade.put("gpaPoints", convertToGPAPoints(avgGrade));
            
            breakdown.add(courseGrade);
        }
        
        result.put("breakdown", breakdown);
        return result;
    }

    @GetMapping("/api/export/{format}")
    public ResponseEntity<byte[]> exportGrades(
            @PathVariable String format,
            Authentication authentication) {
        
        User student = (User) authentication.getPrincipal();
        
        try {
            byte[] data;
            String filename;
            String contentType;
            
            switch (format.toLowerCase()) {
                case "pdf":
                    data = generatePDFTranscript(student);
                    filename = "transcript_" + student.getUsername() + ".pdf";
                    contentType = "application/pdf";
                    break;
                case "excel":
                    data = generateExcelReport(student);
                    filename = "grades_" + student.getUsername() + ".xlsx";
                    contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                    break;
                case "csv":
                    data = generateCSVReport(student);
                    filename = "grades_" + student.getUsername() + ".csv";
                    contentType = "text/csv";
                    break;
                default:
                    return ResponseEntity.badRequest().build();
            }
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(data);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/api/enrollments/{enrollmentId}")
    @ResponseBody
    public ResponseEntity<?> dropCourse(@PathVariable Long enrollmentId, Authentication authentication) {
        User student = (User) authentication.getPrincipal();
        
        return enrollmentService.getEnrollmentById(enrollmentId)
                .filter(enrollment -> enrollment.getStudent().getId().equals(student.getId()))
                .map(enrollment -> {
                    enrollmentService.dropEnrollment(enrollmentId);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Helper methods
    private String convertToLetterGrade(Double score) {
        if (score == null) return "N/A";
        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }

    private double convertToGPAPoints(Double score) {
        if (score == null) return 0.0;
        if (score >= 90) return 4.0;
        if (score >= 80) return 3.0;
        if (score >= 70) return 2.0;
        if (score >= 60) return 1.0;
        return 0.0;
    }

    private byte[] generatePDFTranscript(User student) {
        // Simplified PDF generation - in a real app, use iText or similar
        String content = "TRANSCRIPT\n\nStudent: " + student.getFullName() + "\n\n";
        
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(student);
        for (Enrollment enrollment : enrollments) {
            content += enrollment.getCourse().getCourseCode() + " - " + enrollment.getCourse().getTitle() + "\n";
            Double avgGrade = gradeService.calculateAverageGrade(enrollment);
            content += "Grade: " + (avgGrade != null ? String.format("%.1f", avgGrade) : "N/A") + "\n\n";
        }
        
        return content.getBytes();
    }

    private byte[] generateExcelReport(User student) {
        // Simplified Excel generation - in a real app, use Apache POI
        StringBuilder csv = new StringBuilder();
        csv.append("Course Code,Course Title,Credits,Grade,Letter Grade\n");
        
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(student);
        for (Enrollment enrollment : enrollments) {
            Double avgGrade = gradeService.calculateAverageGrade(enrollment);
            csv.append(enrollment.getCourse().getCourseCode()).append(",")
               .append(enrollment.getCourse().getTitle()).append(",")
               .append(enrollment.getCourse().getCredits()).append(",")
               .append(avgGrade != null ? String.format("%.1f", avgGrade) : "N/A").append(",")
               .append(convertToLetterGrade(avgGrade)).append("\n");
        }
        
        return csv.toString().getBytes();
    }

    private byte[] generateCSVReport(User student) {
        return generateExcelReport(student); // Same format for simplicity
    }
}
