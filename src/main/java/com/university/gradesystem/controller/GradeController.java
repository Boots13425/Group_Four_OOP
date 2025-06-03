package com.university.gradesystem.controller;

import com.university.gradesystem.model.Grade;
import com.university.gradesystem.service.GradeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
public class GradeController {
    private final GradeService gradeService;
    public GradeController(GradeService gradeService) { this.gradeService = gradeService; }

    @GetMapping
    public List<Grade> getAll() { return gradeService.getAll(); }

    @GetMapping("/{id}")
    public Grade getById(@PathVariable Long id) { return gradeService.getById(id); }

    @PostMapping
    public Grade create(@RequestBody Grade grade) { return gradeService.save(grade); }

    @PutMapping("/{id}")
    public Grade update(@PathVariable Long id, @RequestBody Grade grade) {
        grade.setGradeID(id);
        return gradeService.save(grade);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { gradeService.delete(id); }

    // Calculate GPA for a student
    @GetMapping("/calculate-gpa/{studentId}")
    public double calculateGPA(@PathVariable Long studentId) {
        return gradeService.calculateGPA(studentId);
    }

    // Update a grade
    @PutMapping("/{gradeId}/update-grade")
    public Grade updateGrade(@PathVariable Long gradeId, @RequestParam double value,
                             @RequestParam String letter, @RequestParam boolean isFinalized) {
        return gradeService.updateGrade(gradeId, value, letter, isFinalized);
    }

    // Finalize a grade
    @PutMapping("/{gradeId}/finalize")
    public Grade finalizeGrade(@PathVariable Long gradeId) {
        return gradeService.finalizeGrade(gradeId);
    }
}