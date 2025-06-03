package com.university.gradesystem.controller;

import com.university.gradesystem.model.Grade;
import com.university.gradesystem.model.Professor;
import com.university.gradesystem.service.ProfessorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professors")
public class ProfessorController {
    private final ProfessorService professorService;
    public ProfessorController(ProfessorService professorService) { this.professorService = professorService; }

    @GetMapping
    public List<Professor> getAll() { return professorService.getAll(); }

    @GetMapping("/{id}")
    public Professor getById(@PathVariable Long id) { return professorService.getById(id).orElse(null); }

    @PostMapping
    public Professor create(@RequestBody Professor professor) { return professorService.save(professor); }

    @PutMapping("/{id}")
    public Professor update(@PathVariable Long id, @RequestBody Professor professor) {
        professor.setUserID(id);
        return professorService.save(professor);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { professorService.delete(id); }

    // Upload marks
    @PostMapping("/upload-marks")
    public Grade uploadMarks(@RequestParam Long enrollmentId, @RequestParam double value, @RequestParam String letterGrade) {
        return professorService.uploadMarks(enrollmentId, value, letterGrade);
    }

    // Update marks
    @PutMapping("/update-marks/{gradeId}")
    public Grade updateMarks(@PathVariable Long gradeId, @RequestParam double value, @RequestParam String letterGrade) {
        return professorService.updateMarks(gradeId, value, letterGrade);
    }

    // View all grades for a course
    @GetMapping("/course-grades/{courseId}")
    public List<Grade> viewCourseGrades(@PathVariable Long courseId) {
        return professorService.viewCourseGrades(courseId);
    }
}