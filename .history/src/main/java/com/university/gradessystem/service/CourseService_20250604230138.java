package com.university.gradessystem.service;

import com.university.gradessystem.model.Course;
import com.university.gradessystem.model.GradePolicy;
import com.university.gradessystem.model.User;
import com.university.gradessystem.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private EnrollmentService enrollmentService;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Autowired
    @Lazy
    public void setEnrollmentService(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getActiveCourses() {
        return courseRepository.findByActive(true);
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public List<Course> getCoursesByProfessor(User professor) {
        return courseRepository.findByProfessor(professor);
    }

    public List<Course> getActiveCoursesByProfessor(User professor) {
        return courseRepository.findByProfessorAndActive(professor, true);
    }

    public List<Course> getCoursesByDepartment(String department) {
        return courseRepository.findByDepartment(department);
    }

    public List<Course> getCoursesBySemesterAndYear(String semester, String academicYear) {
        return courseRepository.findBySemesterAndAcademicYear(semester, academicYear);
    }

    @Transactional
    public Course createCourse(Course course) {
        // Initialize grade policy with default values if not set
        if (course.getGradePolicy() == null) {
            GradePolicy policy = new GradePolicy();

            // Set default grade scale
            Map<String, Double> defaultScale = new HashMap<>();
            defaultScale.put("A", 90.0);
            defaultScale.put("B", 80.0);
            defaultScale.put("C", 70.0);
            defaultScale.put("D", 60.0);
            defaultScale.put("F", 0.0);

            policy.setGradeScale(defaultScale);
            course.setGradePolicy(policy);
            policy.setCourse(course);
        }

        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    @Transactional
    public void deactivateCourse(Long id) {
        courseRepository.findById(id).ifPresent(course -> {
            course.setActive(false);
            courseRepository.save(course);
        });
    }

    @Transactional
    public void activateCourse(Long id) {
        courseRepository.findById(id).ifPresent(course -> {
            course.setActive(true);
            courseRepository.save(course);
        });
    }

    public long countActiveCourses() {
        return courseRepository.countActiveCourses();
    }

    public long countEnrolledStudents(Course course) {
        if (enrollmentService != null) {
            return enrollmentService.countEnrolledStudents(course);
        }
        return 0;
    }

    public Map<Long, Long> getEnrollmentStatsByCourseIds(List<Long> courseIds) {
        Map<Long, Long> stats = new HashMap<>();
        if (enrollmentService != null) {
            for (Long id : courseIds) {
                courseRepository.findById(id).ifPresent(course -> {
                    stats.put(id, enrollmentService.countEnrolledStudents(course));
                });
            }
        }
        return stats;
    }
}
