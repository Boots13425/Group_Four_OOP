package com.university.gradessystem.controller;

import com.university.gradessystem.model.*;
import com.university.gradessystem.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/professor")
@RequiredArgsConstructor
public class ProfessorController {

    private final UserService userService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final GradeService gradeService;
    private final NotificationService notificationService;

    @GetMapping
    public String professorDashboard(Authentication authentication, Model model) {
        User professor = (User) authentication.getPrincipal();
        List<Course> courses = courseService.getActiveCoursesByProfessor(professor);
        
        // Count total students across all courses
        int totalStudents = 0;
        int gradesSubmitted = 0;
        int totalGrades = 0;
        
        for (Course course : courses) {
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(course);
            totalStudents += enrollments.size();
            
            for (Enrollment enrollment : enrollments) {
                List<Grade> grades = gradeService.getGradesByEnrollment(enrollment);
                totalGrades += grades.size();
                gradesSubmitted += grades.stream().filter(g -> g.getScore() != null).count();
            }
        }
        
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("coursesTeaching", courses.size());
        model.addAttribute("gradesSubmitted", totalGrades > 0 ? (gradesSubmitted * 100 / totalGrades) : 0);
        model.addAttribute("courses", courses);
        
        return "professor";
    }

    @GetMapping("/api/courses")
    @ResponseBody
    public List<Course> getProfessorCourses(Authentication authentication) {
        User professor = (User) authentication.getPrincipal();
        return courseService.getActiveCoursesByProfessor(professor);
    }

    @GetMapping("/api/courses/{courseId}/students")
    @ResponseBody
    public List<Map<String, Object>> getStudentsInCourse(@PathVariable Long courseId) {
        return courseService.getCourseById(courseId)
                .map(course -> {
                    List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(course);
                    return enrollments.stream()
                            .map(enrollment -> {
                                Map<String, Object> studentInfo = new HashMap<>();
                                studentInfo.put("enrollmentId", enrollment.getId());
                                studentInfo.put("studentId", enrollment.getStudent().getId());
                                studentInfo.put("name", enrollment.getStudent().getFullName());
                                
                                // Calculate current grade
                                Double avgGrade = gradeService.calculateAverageGrade(enrollment);
                                studentInfo.put("currentGrade", avgGrade != null ? avgGrade : 0);
                                
                                return studentInfo;
                            })
                            .collect(Collectors.toList());
                })
                .orElse(new ArrayList<>());
    }

    @PostMapping("/api/grades")
    @ResponseBody
    public ResponseEntity<?> addGrade(
            @RequestParam Long enrollmentId,
            @RequestParam String assignmentType,
            @RequestParam String assignmentName,
            @RequestParam Double score,
            Authentication authentication) {
        
        User professor = (User) authentication.getPrincipal();
        
        return enrollmentService.getEnrollmentById(enrollmentId)
                .map(enrollment -> {
                    Grade grade = new Grade();
                    grade.setEnrollment(enrollment);
                    grade.setAssignmentType(assignmentType);
                    grade.setAssignmentName(assignmentName);
                    grade.setScore(score);
                    grade.setGradedDate(LocalDateTime.now());
                    grade.setGrade(professor);
                    
                    Grade savedGrade = gradeService.addGrade(grade);
                    
                    // Send notification to student
                    notificationService.createNotification(
                            enrollment.getStudent(),
                            "New grade posted for " + assignmentName + " in " + enrollment.getCourse().getTitle(),
                            "grade_posted",
                            enrollment.getCourse()
                    );
                    
                    return ResponseEntity.ok(savedGrade);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/api/grades/{gradeId}")
    @ResponseBody
    public ResponseEntity<?> updateGrade(
            @PathVariable Long gradeId,
            @RequestParam Double score,
            @RequestParam(required = false) String comments) {
        
        return gradeService.getGradeById(gradeId)
                .map(grade -> {
                    grade.setScore(score);
                    if (comments != null) {
                        grade.setComments(comments);
                    }
                    grade.setGradedDate(LocalDateTime.now());
                    
                    Grade updatedGrade = gradeService.updateGrade(grade);
                    
                    // Send notification to student
                    notificationService.createNotification(
                            grade.getEnrollment().getStudent(),
                            "Grade updated for " + grade.getAssignmentName() + " in " + grade.getEnrollment().getCourse().getTitle(),
                            "grade_updated",
                            grade.getEnrollment().getCourse()
                    );
                    
                    return ResponseEntity.ok(updatedGrade);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/students/{studentId}/gpa")
    @ResponseBody
    public ResponseEntity<?> calculateStudentGPA(@PathVariable Long studentId) {
        return userService.getUserById(studentId)
                .map(student -> {
                    Double gpa = gradeService.calculateGPA(student);
                    Map<String, Object> result = new HashMap<>();
                    result.put("gpa", gpa);
                    result.put("studentName", student.getFullName());
                    
                    // Get grade breakdown
                    List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(student);
                    List<Map<String, Object>> breakdown = new ArrayList<>();
                    
                    for (Enrollment enrollment : enrollments) {
                        Map<String, Object> courseGrade = new HashMap<>();
                        courseGrade.put("course", enrollment.getCourse().getTitle());
                        courseGrade.put("credits", enrollment.getCourse().getCredits());
                        courseGrade.put("average", gradeService.calculateAverageGrade(enrollment));
                        breakdown.add(courseGrade);
                    }
                    
                    result.put("breakdown", breakdown);
                    return ResponseEntity.ok(result);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/notifications")
    @ResponseBody
    public ResponseEntity<?> sendNotification(
            @RequestParam Long courseId,
            @RequestParam String type,
            @RequestParam String message,
            @RequestParam String recipients) {
        
        return courseService.getCourseById(courseId)
                .map(course -> {
                    List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(course);
                    
                    for (Enrollment enrollment : enrollments) {
                        notificationService.createNotification(
                                enrollment.getStudent(),
                                message,
                                type,
                                course
                        );
                    }
                    
                    return ResponseEntity.ok().body("Notifications sent to " + enrollments.size() + " students");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/grades/search")
    @ResponseBody
    public List<Map<String, Object>> searchGrades(
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) Long courseId,
            Authentication authentication) {
        
        User professor = (User) authentication.getPrincipal();
        List<Course> professorCourses = courseService.getActiveCoursesByProfessor(professor);
        
        List<Map<String, Object>> results = new ArrayList<>();
        
        for (Course course : professorCourses) {
            if (courseId != null && !course.getId().equals(courseId)) {
                continue;
            }
            
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(course);
            
            for (Enrollment enrollment : enrollments) {
                if (studentName != null && !enrollment.getStudent().getFullName().toLowerCase()
                        .contains(studentName.toLowerCase())) {
                    continue;
                }
                
                Map<String, Object> studentGrade = new HashMap<>();
                studentGrade.put("studentId", enrollment.getStudent().getId());
                studentGrade.put("studentName", enrollment.getStudent().getFullName());
                studentGrade.put("course", course.getTitle());
                studentGrade.put("currentGrade", gradeService.calculateAverageGrade(enrollment));
                studentGrade.put("enrollmentId", enrollment.getId());
                
                results.add(studentGrade);
            }
        }
        
        return results;
    }
}
