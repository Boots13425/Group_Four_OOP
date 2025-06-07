package com.university.gradessystem.service;

import com.university.gradessystem.model.Course;
import com.university.gradessystem.model.Enrollment;
import com.university.gradessystem.model.User;
import com.university.gradessystem.repository.CourseRepository;
import com.university.gradessystem.repository.EnrollmentRepository;
import com.university.gradessystem.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
            CourseRepository courseRepository,
            UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public Optional<Enrollment> getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id);
    }

    public List<Enrollment> getEnrollmentsByStudent(User student) {
        return enrollmentRepository.findByStudent(student);
    }

    public List<Enrollment> getEnrollmentsByCourse(Course course) {
        return enrollmentRepository.findByCourse(course);
    }

    public Optional<Enrollment> getEnrollmentByStudentAndCourse(User student, Course course) {
        return enrollmentRepository.findByStudentAndCourse(student, course);
    }

    public List<Enrollment> getEnrollmentsByStudentAndStatus(User student, Enrollment.EnrollmentStatus status) {
        return enrollmentRepository.findByStudentAndStatus(student, status);
    }

    public long countEnrolledStudents(Course course) {
        return enrollmentRepository.countEnrolledStudents(course);
    }

    @Transactional
    public Enrollment enrollStudent(Long studentId, Long courseId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        // Check if student is already enrolled
        Optional<Enrollment> existingEnrollment = enrollmentRepository.findByStudentAndCourse(student, course);
        if (existingEnrollment.isPresent()) {
            return existingEnrollment.get();
        }

        // Check if course has capacity
        long enrolledCount = enrollmentRepository.countEnrolledStudents(course);

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDateTime.now());

        // If course is at capacity, waitlist the student
        if (course.getCapacity() != null && enrolledCount >= course.getCapacity()) {
            enrollment.setStatus(Enrollment.EnrollmentStatus.WAITLISTED);
        } else {
            enrollment.setStatus(Enrollment.EnrollmentStatus.ENROLLED);
        }

        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public void dropEnrollment(Long enrollmentId) {
        enrollmentRepository.findById(enrollmentId).ifPresent(enrollment -> {
            enrollment.setStatus(Enrollment.EnrollmentStatus.DROPPED);
            enrollmentRepository.save(enrollment);
        });
    }

    @Transactional
    public void completeEnrollment(Long enrollmentId) {
        enrollmentRepository.findById(enrollmentId).ifPresent(enrollment -> {
            enrollment.setStatus(Enrollment.EnrollmentStatus.COMPLETED);
            enrollmentRepository.save(enrollment);
        });
    }

    @Transactional
    public void deleteEnrollment(Long id) {
        enrollmentRepository.deleteById(id);
    }

    public long countEnrollments(User student) {
        return enrollmentRepository.countEnrollments(student);
    }
}
