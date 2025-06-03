package com.university.gradesystem.repository;

import com.university.gradesystem.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseName(String courseName);
    Optional<Course> findByCourseID(Long courseID);
    List<Course> findAllBySemester(String semester);
    List<Course> findAllByCredits(int credits);
    List<Course> findByCourseNameContainingIgnoreCase(String courseName);
    List<Course> findByCurrentEnrollmentLessThan(int maxCapacity);
}