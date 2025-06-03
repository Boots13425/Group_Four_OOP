package com.group4.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Administrator class - Represents system administrators
 * Adapted to match the provided Administrator.java structure
 */
@Entity
@DiscriminatorValue("ADMINISTRATOR")
public class Administrator extends User {
    
    @Column(name = "admin_id", unique = true)
    private String adminID;
    
    @Column(name = "department")
    private String department;
    
    @Column(name = "admin_level")
    private String adminLevel;
    
    @Column(name = "permissions")
    private String permissions;
    
    // System settings map (stored as JSON in database)
    @Transient
    private Map<String, String> systemSettings = new HashMap<>();
    
    // Lists to manage users and courses (these would be managed through repositories in real implementation)
    @Transient
    private List<User> users = new ArrayList<>();
    
    @Transient
    private List<Course> courses = new ArrayList<>();
    
    @Transient
    private List<Enrollment> enrollments = new ArrayList<>();
    
    // Constructors
    public Administrator() {
        super();
        setRole("ADMINISTRATOR");
    }

    public Administrator(String username, String name, String email, String password, String adminLevel) {
        super(); // or call the right super-constructor for User if needed
        setUsername(username);
        setName(name);
        setEmail(email);
        setPassword(password);
        this.adminLevel = adminLevel;
        setRole("ADMINISTRATOR");
    }
    
    /**
     * Generate reports based on the type of report requested
     */
    public void generateReports(String reportType) {
        switch(reportType.toLowerCase()) {
            case "user":
                generateUserReport();
                break;
            case "course":
                generateCourseReport();
                break;
            case "enrollment":
                generateEnrollmentReport();
                break;
            case "grade":
                generateGradeReport();
                break;
            default:
                System.out.println("Invalid report type: " + reportType);
        }
    }
    
    private void generateUserReport() {
        System.out.println("=== USER REPORT ===");
        for (User user : users) {
            System.out.println("Username: " + user.getUsername());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Role: " + user.getRole());
            System.out.println("Active: " + user.isActive());
            System.out.println("---------------------------------");
        }
    }
    
    private void generateCourseReport() {
        System.out.println("=== COURSE REPORT ===");
        for (Course course : courses) {
            System.out.println("Course ID: " + course.getCourseID());
            System.out.println("Course Name: " + course.getCourseName());
            System.out.println("Credits: " + course.getCredits());
            System.out.println("Semester: " + course.getSemester());
            System.out.println("Enrollment: " + course.getCurrentEnrollment() + "/" + course.getMaxCapacity());
            System.out.println("---------------------------------");
        }
    }
    
    private void generateEnrollmentReport() {
        System.out.println("=== ENROLLMENT REPORT ===");
        for (Enrollment enrollment : enrollments) {
            System.out.println("Student: " + enrollment.getStudent().getUsername());
            System.out.println("Course: " + enrollment.getCourse().getCourseName());
            System.out.println("Status: " + enrollment.getStatus());
            System.out.println("Date: " + enrollment.getEnrollmentDate());
            System.out.println("---------------------------------");
        }
    }
    
    private void generateGradeReport() {
        System.out.println("=== GRADE REPORT ===");
        for (User user : users) {
            if (user instanceof Student) {
                Student student = (Student) user;
                System.out.println("Student: " + student.getUsername());
                System.out.println("GPA: " + student.getCurrentGPA());
                for (Grade grade : student.getGrades()) {
                    System.out.println("  Course: " + grade.getCourse().getCourseName() + 
                                     ", Grade: " + grade.getLetterGrade());
                }
                System.out.println("---------------------------------");
            }
        }
    }
    
    /**
     * Manage user accounts (add, update, remove)
     */
    public void manageUsers(String action, User user) {
        switch(action.toLowerCase()) {
            case "add":
                users.add(user);
                System.out.println("User added: " + user.getUsername());
                break;
            case "update":
                updateUser(user);
                break;
            case "remove":
                if (users.remove(user)) {
                    System.out.println("User removed: " + user.getUsername());
                } else {
                    System.out.println("User not found: " + user.getUsername());
                }
                break;
            default:
                System.out.println("Invalid action for managing users: " + action);
        }
    }
    
    private void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(user.getUsername())) {
                users.set(i, user);
                System.out.println("User updated: " + user.getUsername());
                return;
            }
        }
        System.out.println("User not found: " + user.getUsername());
    }
    
    /**
     * Manage courses (add, update, delete)
     */
    public void manageCourses(String action, Course course) {
        switch(action.toLowerCase()) {
            case "add":
                courses.add(course);
                System.out.println("Course added: " + course.getCourseName());
                break;
            case "update":
                updateCourse(course);
                break;
            case "delete":
                if (courses.remove(course)) {
                    System.out.println("Course deleted: " + course.getCourseName());
                } else {
                    System.out.println("Course not found: " + course.getCourseName());
                }
                break;
            default:
                System.out.println("Invalid action for managing courses: " + action);
        }
    }
    
    private void updateCourse(Course course) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseID().equals(course.getCourseID())) {
                courses.set(i, course);
                System.out.println("Course updated: " + course.getCourseName());
                return;
            }
        }
        System.out.println("Course not found: " + course.getCourseName());
    }
    
    /**
     * Configure system settings
     */
    public void configureSystem(String setting, String value) {
        systemSettings.put(setting, value);
        System.out.println("Configured " + setting + " to " + value);
    }
    
    /**
     * Retrieve a system setting
     */
    public String getSystemSetting(String setting) {
        return systemSettings.getOrDefault(setting, "Setting not found");
    }
    
    /**
     * Manage course enrollment (enroll, update, remove)
     */
    public void manageCourseEnrollment(String action, Student student, Course course) {
        switch(action.toLowerCase()) {
            case "enroll":
                enrollStudent(student, course);
                break;
            case "update":
                updateEnrollment(student, course);
                break;
            case "remove":
                removeStudentFromCourse(student, course);
                break;
            default:
                System.out.println("Invalid action for managing course enrollment: " + action);
        }
    }
    
    private void enrollStudent(Student student, Course course) {
        if (course.getCurrentEnrollment() < course.getMaxCapacity()) {
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setCourse(course);
            enrollment.setStatus(EnrollmentStatus.ACTIVE);
            enrollment.setEnrollmentDate(java.time.LocalDateTime.now().toString());
            
            enrollments.add(enrollment);
            course.addStudent(student);
            student.getEnrollments().add(enrollment);
            
            System.out.println("Student " + student.getUsername() + 
                             " enrolled in course: " + course.getCourseName());
        } else {
            System.out.println("Course " + course.getCourseName() + " is at full capacity");
        }
    }
    
    private void updateEnrollment(Student student, Course course) {
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getStudent().equals(student)) {
                enrollment.setCourse(course);
                System.out.println("Enrollment updated for student: " + student.getUsername());
                return;
            }
        }
        System.out.println("No enrollment found for student: " + student.getUsername());
    }
    
    private void removeStudentFromCourse(Student student, Course course) {
        enrollments.removeIf(enrollment -> 
            enrollment.getStudent().equals(student) && enrollment.getCourse().equals(course));
        course.removeStudent(student);
        System.out.println("Student " + student.getUsername() + 
                         " removed from course: " + course.getCourseName());
    }
    
    /**
     * Check if admin has specific permission
     */
    public boolean hasPermission(String permission) {
        return "SUPER_ADMIN".equals(adminLevel) || 
               (permissions != null && permissions.contains(permission));
    }
    
    // Getters and Setters
    public String getAdminID() { return adminID; }
    public void setAdminID(String adminID) { this.adminID = adminID; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getAdminLevel() { return adminLevel; }
    public void setAdminLevel(String adminLevel) { this.adminLevel = adminLevel; }
    
    public String getPermissions() { return permissions; }
    public void setPermissions(String permissions) { this.permissions = permissions; }
    
    public Map<String, String> getSystemSettings() { return systemSettings; }
    public void setSystemSettings(Map<String, String> systemSettings) { this.systemSettings = systemSettings; }
    
    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }
    
    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
    
    public List<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; }
}
