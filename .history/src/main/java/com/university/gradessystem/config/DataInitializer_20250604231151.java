package com.university.gradessystem.config;

import com.university.gradessystem.model.*;
import com.university.gradessystem.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;
    private final SystemConfigRepository systemConfigRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            GradeRepository gradeRepository,
            SystemConfigRepository systemConfigRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.gradeRepository = gradeRepository;
        this.systemConfigRepository = systemConfigRepository;
        this.passwordEncoder = passwordEncoder;
    }

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
        if (!userRepository.existsByUsername("admin1")) {
            User admin = new User();
            admin.setUsername("admin1");
            admin.setPassword(passwordEncoder.encode("pass123"));
            admin.setFullName("System Administrator");
            admin.setEmail("admin@university.edu");
            admin.setRole(User.Role.ROLE_ADMIN);
            admin.setActive(true);
            userRepository.save(admin);
        }

        // Create professor users
        if (!userRepository.existsByUsername("prof1")) {
            User prof1 = new User();
            prof1.setUsername("prof1");
            prof1.setPassword(passwordEncoder.encode("pass123"));
            prof1.setFullName("Dr. John Smith");
            prof1.setEmail("john.smith@university.edu");
            prof1.setRole(User.Role.ROLE_PROFESSOR);
            prof1.setActive(true);
            userRepository.save(prof1);
        }

        if (!userRepository.existsByUsername("prof2")) {
            User prof2 = new User();
            prof2.setUsername("prof2");
            prof2.setPassword(passwordEncoder.encode("pass123"));
            prof2.setFullName("Dr. Sarah Johnson");
            prof2.setEmail("sarah.johnson@university.edu");
            prof2.setRole(User.Role.ROLE_PROFESSOR);
            prof2.setActive(true);
            userRepository.save(prof2);
        }

        // Create student users
        if (!userRepository.existsByUsername("student1")) {
            User student1 = new User();
            student1.setUsername("student1");
            student1.setPassword(passwordEncoder.encode("pass123"));
            student1.setFullName("Alice Brown");
            student1.setEmail("alice.brown@student.university.edu");
            student1.setRole(User.Role.ROLE_STUDENT);
            student1.setActive(true);
            userRepository.save(student1);
        }

        if (!userRepository.existsByUsername("student2")) {
            User student2 = new User();
            student2.setUsername("student2");
            student2.setPassword(passwordEncoder.encode("pass123"));
            student2.setFullName("Bob Wilson");
            student2.setEmail("bob.wilson@student.university.edu");
            student2.setRole(User.Role.ROLE_STUDENT);
            student2.setActive(true);
            userRepository.save(student2);
        }

        if (!userRepository.existsByUsername("student3")) {
            User student3 = new User();
            student3.setUsername("student3");
            student3.setPassword(passwordEncoder.encode("pass123"));
            student3.setFullName("Carol Davis");
            student3.setEmail("carol.davis@student.university.edu");
            student3.setRole(User.Role.ROLE_STUDENT);
            student3.setActive(true);
            userRepository.save(student3);
        }
    }

    private void initializeCourses() {
        if (courseRepository.count() == 0) {
            // Get professors
            User prof1 = userRepository.findByUsername("prof1").orElse(null);
            User prof2 = userRepository.findByUsername("prof2").orElse(null);

            if (prof1 != null && prof2 != null) {
                // Course 1
                Course course1 = new Course();
                course1.setCourseCode("CS101");
                course1.setTitle("Introduction to Computer Science");
                course1.setDescription("Basic concepts of computer science and programming");
                course1.setCredits(3);
                course1.setDepartment("Computer Science");
                course1.setProfessor(prof1);
                course1.setCapacity(30);
                course1.setSemester("Spring");
                course1.setAcademicYear("2023-2024");
                course1.setActive(true);

                // Create grade policy
                GradePolicy policy1 = new GradePolicy();
                Map<String, Double> gradeScale1 = new HashMap<>();
                gradeScale1.put("A", 90.0);
                gradeScale1.put("B", 80.0);
                gradeScale1.put("C", 70.0);
                gradeScale1.put("D", 60.0);
                gradeScale1.put("F", 0.0);
                policy1.setGradeScale(gradeScale1);
                policy1.setIncludePlusMinus(true);
                policy1.setWeightByCredits(true);
                policy1.setIncludeTransfer(false);
                policy1.setAutomaticReminders(true);
                course1.setGradePolicy(policy1);
                policy1.setCourse(course1);

                courseRepository.save(course1);

                // Course 2
                Course course2 = new Course();
                course2.setCourseCode("MATH201");
                course2.setTitle("Calculus I");
                course2.setDescription("Differential and integral calculus");
                course2.setCredits(4);
                course2.setDepartment("Mathematics");
                course2.setProfessor(prof2);
                course2.setCapacity(25);
                course2.setSemester("Spring");
                course2.setAcademicYear("2023-2024");
                course2.setActive(true);

                // Create grade policy
                GradePolicy policy2 = new GradePolicy();
                Map<String, Double> gradeScale2 = new HashMap<>();
                gradeScale2.put("A", 90.0);
                gradeScale2.put("B", 80.0);
                gradeScale2.put("C", 70.0);
                gradeScale2.put("D", 60.0);
                gradeScale2.put("F", 0.0);
                policy2.setGradeScale(gradeScale2);
                policy2.setIncludePlusMinus(true);
                policy2.setWeightByCredits(true);
                policy2.setIncludeTransfer(false);
                policy2.setAutomaticReminders(true);
                course2.setGradePolicy(policy2);
                policy2.setCourse(course2);

                courseRepository.save(course2);

                // Course 3
                Course course3 = new Course();
                course3.setCourseCode("CS201");
                course3.setTitle("Data Structures");
                course3.setDescription("Advanced data structures and algorithms");
                course3.setCredits(3);
                course3.setDepartment("Computer Science");
                course3.setProfessor(prof1);
                course3.setCapacity(20);
                course3.setSemester("Spring");
                course3.setAcademicYear("2023-2024");
                course3.setActive(true);

                // Create grade policy
                GradePolicy policy3 = new GradePolicy();
                Map<String, Double> gradeScale3 = new HashMap<>();
                gradeScale3.put("A", 90.0);
                gradeScale3.put("B", 80.0);
                gradeScale3.put("C", 70.0);
                gradeScale3.put("D", 60.0);
                gradeScale3.put("F", 0.0);
                policy3.setGradeScale(gradeScale3);
                policy3.setIncludePlusMinus(true);
                policy3.setWeightByCredits(true);
                policy3.setIncludeTransfer(false);
                policy3.setAutomaticReminders(true);
                course3.setGradePolicy(policy3);
                policy3.setCourse(course3);

                courseRepository.save(course3);
            }
        }
    }

    private void initializeEnrollments() {
        if (enrollmentRepository.count() == 0) {
            // Get students and courses
            User student1 = userRepository.findByUsername("student1").orElse(null);
            User student2 = userRepository.findByUsername("student2").orElse(null);
            User student3 = userRepository.findByUsername("student3").orElse(null);

            List<Course> courses = courseRepository.findAll();

            if (student1 != null && student2 != null && student3 != null && courses.size() >= 3) {
                Course cs101 = courses.get(0);
                Course math201 = courses.get(1);
                Course cs201 = courses.get(2);

                // Enroll student1 in CS101 and MATH201
                createEnrollment(student1, cs101);
                createEnrollment(student1, math201);

                // Enroll student2 in CS101 and CS201
                createEnrollment(student2, cs101);
                createEnrollment(student2, cs201);

                // Enroll student3 in MATH201 and CS201
                createEnrollment(student3, math201);
                createEnrollment(student3, cs201);
            }
        }
    }

    private void createEnrollment(User student, Course course) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setStatus(Enrollment.EnrollmentStatus.ENROLLED);
        enrollmentRepository.save(enrollment);
    }

    private void initializeGrades() {
        if (gradeRepository.count() == 0) {
            User prof1 = userRepository.findByUsername("prof1").orElse(null);
            List<Enrollment> enrollments = enrollmentRepository.findAll();

            if (prof1 != null && !enrollments.isEmpty()) {
                // Add sample grades for first few enrollments
                for (int i = 0; i < Math.min(3, enrollments.size()); i++) {
                    Enrollment enrollment = enrollments.get(i);

                    Grade grade1 = new Grade();
                    grade1.setEnrollment(enrollment);
                    grade1.setAssignmentType("exam");
                    grade1.setAssignmentName("Midterm Exam");
                    grade1.setScore(85.0 + (i * 5));
                    grade1.setGradedDate(LocalDateTime.now().minusDays(7));
                    grade1.setGradedBy(prof1);
                    gradeRepository.save(grade1);

                    Grade grade2 = new Grade();
                    grade2.setEnrollment(enrollment);
                    grade2.setAssignmentType("assignment");
                    grade2.setAssignmentName("Programming Assignment 1");
                    grade2.setScore(90.0 + (i * 2));
                    grade2.setGradedDate(LocalDateTime.now().minusDays(3));
                    grade2.setGradedBy(prof1);
                    gradeRepository.save(grade2);
                }
            }
        }
    }

    private void initializeSystemConfig() {
        if (systemConfigRepository.count() == 0) {
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
            config.setBackupTime(java.time.LocalTime.of(2, 0));
            systemConfigRepository.save(config);
        }
    }
}
