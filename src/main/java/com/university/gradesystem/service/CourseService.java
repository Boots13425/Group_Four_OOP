package com.university.gradesystem.service;

import com.university.gradesystem.model.Course;
import com.university.gradesystem.model.Enrollment;
import com.university.gradesystem.model.Student;
import com.university.gradesystem.repository.CourseRepository;
import com.university.gradesystem.repository.EnrollmentRepository;
import com.university.gradesystem.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final StudentRepository studentRepo;

    public CourseService(CourseRepository courseRepo, EnrollmentRepository enrollmentRepo, StudentRepository studentRepo) {
        this.courseRepo = courseRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.studentRepo = studentRepo;
    }

    public List<Course> getAll() { return courseRepo.findAll(); }

    public Optional<Course> getById(Long id) { return courseRepo.findById(id); }

    public Course save(Course course) { return courseRepo.save(course); }

    public void delete(Long id) { courseRepo.deleteById(id); }

    // --- Custom ---

    // Add student to course
    public Enrollment addStudent(Long courseId, Long studentId) {
        Course course = courseRepo.findById(courseId).orElseThrow();
        Student student = studentRepo.findById(studentId).orElseThrow();
        if (course.getCurrentEnrollment() < course.getMaxCapacity()) {
            Enrollment enrollment = new Enrollment();
            enrollment.setCourse(course);
            enrollment.setStudent(student);
            enrollment.setEnrollmentDate(java.time.LocalDate.now());
            enrollment.setStatus("ENROLLED");
            course.setCurrentEnrollment(course.getCurrentEnrollment() + 1);
            courseRepo.save(course);
            return enrollmentRepo.save(enrollment);
        }
        throw new RuntimeException("Course is full");
    }

    // Remove student from course
    public void removeStudent(Long courseId, Long studentId) {
        Enrollment enrollment = enrollmentRepo.findAll().stream()
                .filter(e -> e.getCourse().getCourseID().equals(courseId)
                        && e.getStudent().getUserID().equals(studentId))
                .findFirst().orElseThrow();
        enrollmentRepo.delete(enrollment);
        Course course = enrollment.getCourse();
        course.setCurrentEnrollment(course.getCurrentEnrollment() - 1);
        courseRepo.save(course);
    }

    // Update course info
    public Course updateCourseInfo(Long courseId, String name, int credits, String semester, int maxCapacity) {
        Course course = courseRepo.findById(courseId).orElseThrow();
        course.setCourseName(name);
        course.setCredits(credits);
        course.setSemester(semester);
        course.setMaxCapacity(maxCapacity);
        return courseRepo.save(course);
    }
}