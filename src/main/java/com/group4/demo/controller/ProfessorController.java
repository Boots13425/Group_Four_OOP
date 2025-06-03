package  com.group4.demo.controller;

import com.group4.demo.model.Student;
import com.group4.demo.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Professor Controller - Handles all professor-related REST API endpoints
 * This implements all the professor scenarios from your sequence diagrams
 */
@RestController
@RequestMapping("/professors")
@CrossOrigin(origins = "*")
public class ProfessorController {
    
    @Autowired
    private ProfessorService professorService;
    
    /**
     * PROFESSOR LOGIN SCENARIO - Dashboard
     * GET /professors/{professorId}/dashboard
     * 
     * This implements the professor login flow with course loading
     */
    @GetMapping("/{professorId}/dashboard")
    public ResponseEntity<?> getProfessorDashboard(@PathVariable Long professorId) {
        try {
            ProfessorService.ProfessorDashboardResponse response = professorService
                .getProfessorDashboard(professorId);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * VIEW STUDENT GRADES SCENARIO
     * GET /professors/{professorId}/courses/{courseId}/grades
     * 
     * This implements the grade viewing flow for professors
     */
    @GetMapping("/{professorId}/courses/{courseId}/grades")
    public ResponseEntity<?> viewStudentGrades(
            @PathVariable Long professorId,
            @PathVariable Long courseId) {
        try {
            ProfessorService.CourseGradesResponse response = professorService
                .viewStudentGrades(professorId, courseId);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * UPLOAD STUDENT MARKS SCENARIO
     * POST /professors/{professorId}/courses/{courseId}/grades
     * 
     * This implements the grade upload flow with validation
     */
    @PostMapping("/{professorId}/courses/{courseId}/grades")
    public ResponseEntity<?> uploadStudentMarks(
            @PathVariable Long professorId,
            @PathVariable Long courseId,
            @RequestBody GradeUploadRequest request) {
        try {
            ProfessorService.GradeUploadResponse response = professorService
                .uploadStudentMarks(professorId, courseId, request.getGrades(), request.getTerm());
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ProfessorService.GradeUploadResponse(false, e.getMessage()));
        }
    }
    
    /**
     * UPDATE STUDENT MARKS SCENARIO
     * PUT /professors/{professorId}/grades/{gradeId}
     * 
     * This implements the grade update flow with logging
     */
    @PutMapping("/{professorId}/grades/{gradeId}")
    public ResponseEntity<?> updateStudentMark(
            @PathVariable Long professorId,
            @PathVariable Long gradeId,
            @RequestBody GradeUpdateRequest request) {
        try {
            ProfessorService.GradeUpdateResponse response = professorService
                .updateStudentMark(professorId, gradeId, request.getNewScore());
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ProfessorService.GradeUpdateResponse(false, e.getMessage()));
        }
    }
    
    /**
     * GET ENROLLED STUDENTS
     * GET /professors/{professorId}/courses/{courseId}/students
     * 
     * This supports the grade management scenarios
     */
    @GetMapping("/{professorId}/courses/{courseId}/students")
    public ResponseEntity<?> getEnrolledStudents(
            @PathVariable Long professorId,
            @PathVariable Long courseId) {
        try {
            List<Student> students = professorService.getEnrolledStudents(professorId, courseId);
            return ResponseEntity.ok(students);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Request classes for API endpoints
     */
    public static class GradeUploadRequest {
        private List<ProfessorService.GradeUploadRequest> grades;
        private String term;
        
        public List<ProfessorService.GradeUploadRequest> getGrades() { return grades; }
        public void setGrades(List<ProfessorService.GradeUploadRequest> grades) { this.grades = grades; }
        
        public String getTerm() { return term; }
        public void setTerm(String term) { this.term = term; }

        private Long studentId;
        private Double score;

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }

        public Double getValue() { return score; }
        public void setScore(Double score) { this.score = score; }

        // Add this getter for compatibility with .getScore() usage
        public Double getScore() { return score; }

    }
    
    public static class GradeUpdateRequest {
        private Double newScore;
        
        public Double getNewScore() { return newScore; }
        public void setNewScore(Double newScore) { this.newScore = newScore; }

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
