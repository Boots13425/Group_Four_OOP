package com.university.gradessystem.config;

import com.university.gradessystem.model.*;
import com.university.gradessystem.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final GradeService gradeService;
    private final SystemConfigService systemConfigService;

    @Override
    public void run(String... args) throws Exception {
        initializeUsers();
        initializeCourses();
        initializeEnrollments();
        initializeGrades();
        initializeSystemConfig();
    }

    private void initializeUsers() {
        // Create admin user
        if (!userService.existsByUsername("admin1")) {
            User admin = new User();
            admin.setUsername("admin1");
            admin.setPassword("pass123");
            admin.setFullName("System Administrator");
            admin.setEmail("admin@university.edu");
            admin.setRole(User.Role.ROLE_ADMIN);
            userService.createUser(admin);
        }

        // Create professor users
        if (!userService.existsByUsername("prof1")) {
            User prof1 = new User();
            prof1.setUsername("prof1");
            prof1.setPassword("pass123");
            prof1.setFullName("Dr. John Smith");
            prof1.setEmail("john.smith@university.edu");
            prof1.setRole(User.Role.ROLE_PROFESSOR);
            userService.createUser(prof1);
        }

        if (!userService.existsByUsername("prof2")) {
            User prof2 = new User();
            prof2.setUsername("prof2");
            prof2.setPassword("pass123");
            prof2.setFullName("Dr. Sarah Johnson");
            prof2.setEmail("sarah.johnson@university.edu");
            prof2.setRole(User.Role.ROLE_PROFESSOR);
            userService.createUser(prof2);
        }

        // Create student users
        if (!userService.existsByUsername("student1")) {
            User student1 = new User();
            student1.setUsername("student1");
            student1.setPassword("pass123");
            student1.setFullName("Alice Brown");
            student1.setEmail("alice.brown@student.university.edu");
            student1.setRole(User.Role.ROLE_STUDENT);
            userService.createUser(student1);
        }

        if (!userService.existsByUsername("student2")) {
            User student2 = new User();
            student2.setUsername("student2");
            student2.setPassword("pass123");
            student2.setFullName("Bob Wilson");
            student2.setEmail("bob.wilson@student.university.edu");
            student2.setRole(User.Role.ROLE_STUDENT);
            userService.createUser(student2);
        }

        if (!userService.existsByUsername("student3")) {
            User student3 = new User();
            student3.setUsername("student3");
            student3.setPassword("pass123");
            student3.setFullName("Carol Davis");
            student3.setEmail("carol.davis@student.university.edu");
            student3.setRole(User.Role.ROLE_STUDENT);
            userService.createUser(student3);
        }
    }

    private void initializeCourses() {
        User prof1 = userService.loadUserByUsername("prof1");
        User prof2 = userService.loadUserByUsername("prof2");

        // Create courses
        if (courseService.getAllCourses().isEmpty()) {
            Course course1 = new Course();
            course1.setCourseCode("CS101");
            course1.setTitle("Introduction to Computer Science");
            course1.setDescription("Basic concepts of computer science and programming");
            course1.setCredits(3);
            course1.setDepartment("Computer Science");
            course1.setProfessor((User) prof1);
            course1.setCapacity(30);
            course1.setSemester("Spring");
            course1.setAcademicYear("2023-2024");
            courseService.createCourse(course1);

            Course course2 = new Course();
            course2.setCourseCode("MATH201");
            course2.setTitle("Calculus I");
            course2.setDescription("Differential and integral calculus");
            course2.setCredits(4);
            course2.setDepartment("Mathematics");
            course2.setProfessor((User) prof2);
            course2.setCapacity(25);
            course2.setSemester("Spring");
            course2.setAcademicYear("2023-2024");
            courseService.createCourse(course2);

            Course course3 = new Course();
            course3.setCourseCode("CS201");
            course3.setTitle("Data Structures");
            course3.setDescription("Advanced data structures and algorithms");
            course3.setCredits(3);
            course3.setDepartment("Computer Science");
            course3.setProfessor((User) prof1);
            course3.setCapacity(20);
            course3.setSemester("Spring");
            course3.setAcademicYear("2023-2024");
            courseService.createCourse(course3);
        }
    }

    private void initializeEnrollments() {
        if (enrollmentService.getAllEnrollments().isEmpty()) {
            User student1 = (User) userService.loadUserByUsername("student1");
            User student2 = (User) userService.loadUserByUsername("student2");
            User student3 = (User) userService.loadUserByUsername("student3");

            // Enroll students in courses
            enrollmentService.enrollStudent(student1.getId(), 1L); // CS101
            enrollmentService.enrollStudent(student1.getId(), 2L); // MATH201
            
            enrollmentService.enrollStudent(student2.getId(), 1L); // CS101
            enrollmentService.enrollStudent(student2.getId(), 3L); // CS201
            
            enrollmentService.enrollStudent(student3.getId(), 2L); // MATH201
            enrollmentService.enrollStudent(student3.getId(), 3L); // CS201
        }
    }

    private void initializeGrades() {
        if (gradeService.getAllGrades().isEmpty()) {
            User prof1 = (User) userService.loadUserByUsername("prof1");
            
            // Add some sample grades
            Grade grade1 = new Grade();
            grade1.setEnrollment(enrollmentService.getEnrollmentById(1L).orElse(null));
            grade1.setAssignmentType("exam");
            grade1.setAssignmentName("Midterm Exam");
            grade1.setScore(85.0);
            grade1.setGradedDate(LocalDateTime.now().minusDays(7));
            grade1.setGradedBy(prof1);
            gradeService.addGrade(grade1);

            Grade grade2 = new Grade();
            grade2.setEnrollment(enrollmentService.getEnrollmentById(1L).orElse(null));
            grade2.setAssignmentType("assignment");
            grade2.setAssignmentName("Programming Assignment 1");
            grade2.setScore(92.0);
            grade2.setGradedDate(LocalDateTime.now().minusDays(3));
            grade2.setGradedBy(prof1);
            gradeService.addGrade(grade2);

            Grade grade3 = new Grade();
            grade3.setEnrollment(enrollmentService.getEnrollmentById(2L).orElse(null));
            grade3.setAssignmentType("quiz");
            grade3.setAssignmentName("Quiz 1");
            grade3.setScore(78.0);
            grade3.setGradedDate(LocalDateTime.now().minusDays(5));
            grade3.setGradedBy(prof1);
            gradeService.addGrade(grade3);
        }
    }

    private void initializeSystemConfig() {
        if (systemConfigService.getSystemConfig().isEmpty()) {
            SystemConfig config = new SystemConfig();
            config.setUniversityName("Sample University");
            config.setAcademicYear("2023-2024");
            config.setCurrentSemester("Spring");
            config.setRequireUppercase(true);
            config.setRequireLowercase(true);
            config.setRequireNumbers(true);
            config.setRequireSpecialChars(false);
            config.setMinimumLength(8);
            config.setSessionTimeoutMinutes(30);
            config.setMaxFailedLogins(5);
            config.setBackupFrequency("DAILY");
            config.setRetentionPeriodDays(30);
            systemConfigService.saveSystemConfig(config);
        }
    }
}
