package com.university.gradesystem.controller;

import com.university.gradesystem.model.Course;
import com.university.gradesystem.model.Enrollment;
import com.university.gradesystem.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;
    public CourseController(CourseService courseService) { this.courseService = courseService; }

    @GetMapping
    public List<Course> getAll() { return courseService.getAll(); }

    @GetMapping("/{id}")
    public Course getById(@PathVariable Long id) { return courseService.getById(id).orElse(null); }

    @PostMapping
    public Course create(@RequestBody Course course) { return courseService.save(course); }

    @PutMapping("/{id}")
    public Course update(@PathVariable Long id, @RequestBody Course course) {
        course.setCourseID(id);
        return courseService.save(course);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { courseService.delete(id); }

    // Add student to course
    @PostMapping("/{courseId}/add-student")
    public Enrollment addStudent(@PathVariable Long courseId, @RequestParam Long studentId) {
        return courseService.addStudent(courseId, studentId);
    }

    // Remove student from course
    @DeleteMapping("/{courseId}/remove-student")
    public void removeStudent(@PathVariable Long courseId, @RequestParam Long studentId) {
        courseService.removeStudent(courseId, studentId);
    }

    // Update course info
    @PutMapping("/{courseId}/update-info")
    public Course updateCourseInfo(@PathVariable Long courseId, @RequestParam String name,
                                   @RequestParam int credits, @RequestParam String semester,
                                   @RequestParam int maxCapacity) {
        return courseService.updateCourseInfo(courseId, name, credits, semester, maxCapacity);
    }
}