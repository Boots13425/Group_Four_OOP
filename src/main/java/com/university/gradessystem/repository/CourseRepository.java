package com.university.gradessystem.repository;

import com.university.gradessystem.model.Course;
import com.university.gradessystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    List<Course> findByProfessor(User professor);
    
    List<Course> findByActive(boolean active);
    
    List<Course> findByDepartment(String department);
    
    List<Course> findByProfessorAndActive(User professor, boolean active);
    
    List<Course> findBySemesterAndAcademicYear(String semester, String academicYear);
    
    @Query("SELECT COUNT(c) FROM Course c WHERE c.active = true")
    long countActiveCourses();
}
