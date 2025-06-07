package com.university.gradessystem.repository;

import com.university.gradessystem.model.Course;
import com.university.gradessystem.model.Enrollment;
import com.university.gradessystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    List<Enrollment> findByStudent(User student);
    
    List<Enrollment> findByCourse(Course course);
    
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);
    
    List<Enrollment> findByStudentAndStatus(User student, Enrollment.EnrollmentStatus status);
    
    List<Enrollment> findByCourseAndStatus(Course course, Enrollment.EnrollmentStatus status);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course = :course AND e.status = 'ENROLLED'")
    long countEnrolledStudents(@Param("course") Course course);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.student = :student")
    long countEnrollments(@Param("student") User student);
}
