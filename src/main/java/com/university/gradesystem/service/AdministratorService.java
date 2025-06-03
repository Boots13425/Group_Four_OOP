package com.university.gradesystem.service;

import com.university.gradesystem.model.*;
import com.university.gradesystem.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdministratorService {
    private final AdministratorRepository adminRepo;
    private final UserRepository userRepo;
    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final SystemConfigRepository configRepo;
    // Optionally add these if you have them; otherwise, see comments in generateReports
    private final StudentRepository studentRepo;
    private final ProfessorRepository professorRepo;

    public AdministratorService(
            AdministratorRepository adminRepo,
            UserRepository userRepo,
            CourseRepository courseRepo,
            EnrollmentRepository enrollmentRepo,
            SystemConfigRepository configRepo,
            StudentRepository studentRepo,
            ProfessorRepository professorRepo
    ) {
        this.adminRepo = adminRepo;
        this.userRepo = userRepo;
        this.courseRepo = courseRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.configRepo = configRepo;
        this.studentRepo = studentRepo;
        this.professorRepo = professorRepo;
    }

    // -------------------- ADMINISTRATOR CRUD --------------------
    public List<Administrator> getAll() { return adminRepo.findAll(); }

    public Optional<Administrator> getById(Long id) { return adminRepo.findById(id); }

    public Administrator save(Administrator admin) { return adminRepo.save(admin); }

    public void delete(Long id) { adminRepo.deleteById(id); }

    // -------------------- USER MANAGEMENT --------------------
    public List<User> getAllUsers() { return userRepo.findAll(); }

    public Optional<User> getUserById(Long id) { return userRepo.findById(id); }

    public User addUser(User user) { return userRepo.save(user); }

    public User updateUser(Long id, User user) {
        user.setUserID(id);
        return userRepo.save(user);
    }

    public void deleteUser(Long id) { userRepo.deleteById(id); }

    // -------------------- COURSE MANAGEMENT --------------------
    public List<Course> getAllCourses() { return courseRepo.findAll(); }

    public Optional<Course> getCourseById(Long id) { return courseRepo.findById(id); }

    public Course addCourse(Course course) { return courseRepo.save(course); }

    public Course updateCourse(Long id, Course course) {
        course.setCourseID(id);
        return courseRepo.save(course);
    }

    public void deleteCourse(Long id) { courseRepo.deleteById(id); }

    // -------------------- ENROLLMENT MANAGEMENT --------------------
    public List<Enrollment> getAllEnrollments() { return enrollmentRepo.findAll(); }

    public Optional<Enrollment> getEnrollmentById(Long id) { return enrollmentRepo.findById(id); }

    public Enrollment addEnrollment(Enrollment enrollment) { return enrollmentRepo.save(enrollment); }

    public Enrollment updateEnrollment(Long id, Enrollment enrollment) {
        enrollment.setEnrollmentID(id);
        return enrollmentRepo.save(enrollment);
    }

    public void deleteEnrollment(Long id) { enrollmentRepo.deleteById(id); }

    // -------------------- SYSTEM CONFIGURATION --------------------
    public List<SystemConfig> getAllConfigs() { return configRepo.findAll(); }

    public Optional<SystemConfig> getConfigById(Long id) { return configRepo.findById(id); }

    public SystemConfig addConfig(SystemConfig config) { return configRepo.save(config); }

    public SystemConfig updateConfig(Long id, SystemConfig config) {
        config.setConfigID(id);
        return configRepo.save(config);
    }

    public void deleteConfig(Long id) { configRepo.deleteById(id); }

    // -------------------- REPORTS --------------------
    public String generateReports() {
        // If you have StudentRepository and ProfessorRepository, use their count methods:
        long totalStudents = studentRepo != null ? studentRepo.count() : userRepo.findAll().stream().filter(u -> u instanceof Student).count();
        long totalProfessors = professorRepo != null ? professorRepo.count() : userRepo.findAll().stream().filter(u -> u instanceof Professor).count();
        long totalAdmins = adminRepo.count();
        long totalCourses = courseRepo.count();
        long totalEnrollments = enrollmentRepo.count();
        return "Total Students: " + totalStudents + "\n"
                + "Total Professors: " + totalProfessors + "\n"
                + "Total Admins: " + totalAdmins + "\n"
                + "Total Courses: " + totalCourses + "\n"
                + "Total Enrollments: " + totalEnrollments;
    }

    // -------------------- SHORTCUTS TO MANAGEMENT (for controllers) --------------------
    public List<User> manageUsers() { return getAllUsers(); }
    public List<Course> manageCourses() { return getAllCourses(); }
    public List<Enrollment> manageCourseEnrollment() { return getAllEnrollments(); }
    public SystemConfig configureSystem(SystemConfig config) { return addConfig(config); }
}