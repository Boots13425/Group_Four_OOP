package com.university.gradesystem.controller;

import com.university.gradesystem.model.Enrollment;
import com.university.gradesystem.model.Grade;
import com.university.gradesystem.model.Student;
import com.university.gradesystem.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService studentService;
    public StudentController(StudentService studentService) { this.studentService = studentService; }

    @GetMapping
    public List<Student> getAll() { return studentService.getAll(); }

    @GetMapping("/{id}")
    public Student getById(@PathVariable Long id) { return studentService.getById(id).orElse(null); }

    @PostMapping
    public Student create(@RequestBody Student student) { return studentService.save(student); }

    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @RequestBody Student student) {
        student.setUserID(id);
        return studentService.save(student);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { studentService.delete(id); }

    // View all grades for a student
    @GetMapping("/{id}/grades")
    public List<Grade> viewGrades(@PathVariable Long id) {
        return studentService.viewGrades(id);
    }

    // Export all grades for a student as CSV
    @GetMapping("/{id}/grades/export")
    public String exportGradeData(@PathVariable Long id) {
        return studentService.exportGradeData(id);
    }

    // View historical grades for a student for a semester
    @GetMapping("/{id}/grades/historical")
    public List<Grade> viewHistoricalGrades(@PathVariable Long id, @RequestParam String semester) {
        return studentService.viewHistoricalGrades(id, semester);
    }

    // Register student for a course
    @PostMapping("/{id}/register")
    public Enrollment registerCourse(@PathVariable Long id, @RequestParam Long courseId) {
        return studentService.registerCourse(id, courseId);
    }
}