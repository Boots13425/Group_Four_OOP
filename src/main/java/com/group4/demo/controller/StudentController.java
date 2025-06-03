package com.group4.demo.controller;

import com.group4.demo.model.Course;
import com.group4.demo.service.ExportService;
import com.group4.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Student Controller - Handles all student-related REST API endpoints
 * This implements all the student scenarios from your sequence diagrams
 */
@RestController
@RequestMapping("/students")
@CrossOrigin(origins = "*")
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private ExportService exportService;
    
    /**
     * COURSE REGISTRATION SCENARIO
     * POST /students/{studentId}/enroll
     * 
     * This implements the complete course registration flow
     */
    @PostMapping("/{studentId}/enroll")
    public ResponseEntity<?> enrollInCourse(
            @PathVariable Long studentId,
            @RequestBody EnrollmentRequest request) {
        try {
            StudentService.EnrollmentResponse response = studentService
                .registerForCourse(studentId, request.getCourseId(), request.getTerm());
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new StudentService.EnrollmentResponse(false, e.getMessage()));
        }
    }
    
    /**
     * DROP COURSE SCENARIO
     * DELETE /students/{studentId}/courses/{courseId}
     */
    @DeleteMapping("/{studentId}/courses/{courseId}")
    public ResponseEntity<?> dropCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        try {
            StudentService.EnrollmentResponse response = studentService
                .dropCourse(studentId, courseId);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new StudentService.EnrollmentResponse(false, e.getMessage()));
        }
    }
    
    /**
     * VIEW GRADES SCENARIO
     * GET /students/{studentId}/grades
     * 
     * This implements the grade viewing flow with GPA calculation
     */
    @GetMapping("/{studentId}/grades")
    public ResponseEntity<?> viewCurrentGrades(@PathVariable Long studentId) {
        try {
            StudentService.StudentGradesResponse response = studentService
                .viewCurrentGrades(studentId);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * VIEW HISTORICAL GRADES SCENARIO
     * GET /students/{studentId}/grades/history
     * 
     * This implements the historical grade viewing with term organization
     */
    @GetMapping("/{studentId}/grades/history")
    public ResponseEntity<?> viewHistoricalGrades(@PathVariable Long studentId) {
        try {
            StudentService.HistoricalGradesResponse response = studentService
                .viewHistoricalGrades(studentId);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * EXPORT GRADES SCENARIO - PDF
     * GET /students/{studentId}/grades/export/pdf
     * 
     * This implements the PDF export flow from your sequence diagram
     */
    @GetMapping("/{studentId}/grades/export/pdf")
    public ResponseEntity<?> exportGradesToPDF(@PathVariable Long studentId) {
        try {
            ExportService.ExportResponse response = exportService
                .exportGradesToPDF(studentId);
            
            if (response.isSuccess()) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", 
                    "transcript_student_" + studentId + ".pdf");
                
                return ResponseEntity.ok()
                    .headers(headers)
                    .body(response.getFileData());
            } else {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse(response.getMessage()));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Error exporting PDF: " + e.getMessage()));
        }
    }
    
    /**
     * EXPORT GRADES SCENARIO - CSV
     * GET /students/{studentId}/grades/export/csv
     * 
     * This implements the CSV export flow from your sequence diagram
     */
    @GetMapping("/{studentId}/grades/export/csv")
    public ResponseEntity<?> exportGradesToCSV(@PathVariable Long studentId) {
        try {
            ExportService.ExportResponse response = exportService
                .exportGradesToCSV(studentId);
            
            if (response.isSuccess()) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType("text/csv"));
                headers.setContentDispositionFormData("attachment", 
                    "transcript_student_" + studentId + ".csv");
                
                return ResponseEntity.ok()
                    .headers(headers)
                    .body(response.getFileData());
            } else {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse(response.getMessage()));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Error exporting CSV: " + e.getMessage()));
        }
    }
    
    /**
     * GET AVAILABLE COURSES
     * GET /students/courses/available
     * 
     * This supports the course registration scenario
     */
    @GetMapping("/courses/available")
    public ResponseEntity<?> getAvailableCoursesForEnrollment() {
        try {
            List<Course> courses = studentService.getAvailableCoursesForEnrollment();
            return ResponseEntity.ok(courses);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * EXPORT GRADES BY TERM
     * GET /students/{studentId}/grades/export/{term}
     */
    @GetMapping("/{studentId}/grades/export/{term}")
    public ResponseEntity<?> exportGradesByTerm(
            @PathVariable Long studentId,
            @PathVariable String term,
            @RequestParam(defaultValue = "PDF") String format) {
        try {
            ExportService.ExportResponse response = exportService
                .exportGradesByTerm(studentId, term, format);
            
            if (response.isSuccess()) {
                HttpHeaders headers = new HttpHeaders();
                
                if ("PDF".equalsIgnoreCase(format)) {
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("attachment", 
                        "transcript_" + term + "_student_" + studentId + ".pdf");
                } else {
                    headers.setContentType(MediaType.parseMediaType("text/csv"));
                    headers.setContentDispositionFormData("attachment", 
                        "transcript_" + term + "_student_" + studentId + ".csv");
                }
                
                return ResponseEntity.ok()
                    .headers(headers)
                    .body(response.getFileData());
            } else {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse(response.getMessage()));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Error exporting grades: " + e.getMessage()));
        }
    }
    
    /**
     * Request and Response classes
     */
    public static class EnrollmentRequest {
        private Long courseId;
        private String term;
        
        public Long getCourseId() { return courseId; }
        public void setCourseId(Long courseId) { this.courseId = courseId; }
        
        public String getTerm() { return term; }
        public void setTerm(String term) { this.term = term; }
    }
    
    public static class ErrorResponse {
        private String error;
        private long timestamp;
        
        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getError() { return error; }
        public long getTimestamp() { return timestamp; }
    }
}
