package com.group4.demo.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Professor class - Represents professors in the system
 * Adapted to match the provided Professor.java structure
 */
@Entity
@DiscriminatorValue("PROFESSOR")
public class Professor extends User {

    @Column(name = "professor_id", unique = true)
    private String professorID;

    @Column(name = "department")
    private String department;

    @Column(name = "office_location")
    private String officeLocation;

    // One professor can teach many courses
    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Course> courses = new ArrayList<>();

    // --- Constructors ---

    public Professor() {
        super();
        setRole("PROFESSOR");
    }

    /**
     * Full constructor for ORM and other uses.
     */
    public Professor(String userID, String username, String password, String name, String email,
                     String professorID, String department, String officeLocation) {
        super(userID, username, password, name, email, "PROFESSOR");
        this.professorID = professorID;
        this.department = department;
        this.officeLocation = officeLocation;
    }

    /**
     * Convenience constructor matching typical service-layer creation.
     * (username, password, name, email, professorID, department)
     */
    public Professor(String username, String password, String name, String email,
                     String professorID, String department) {
        super(null, username, password, name, email, "PROFESSOR");
        this.professorID = professorID;
        this.department = department;
        this.officeLocation = null;
    }

    // --- Business logic methods ---
    public void uploadMarks(Course course, Student student, float gradeValue) {
        if (courses.contains(course)) {
            Grade grade = new Grade();
            grade.setStudent(student);
            grade.setCourse(course);
            grade.setValue(gradeValue);
            grade.calculateLetterGrade();
            grade.setTimestamp(java.time.LocalDateTime.now());

            student.getGrades().add(grade);
            student.calculateGPA();

            System.out.println("Grade uploaded for student " + student.getUsername() +
                    " in course " + course.getCourseName() + ": " + gradeValue);
        } else {
            System.out.println("Professor does not teach this course: " + course.getCourseName());
        }
    }

    public void updateMarks(Grade grade, float newValue) {
        if (courses.contains(grade.getCourse())) {
            float oldValue = grade.getValue();
            grade.setValue(newValue);
            grade.calculateLetterGrade();
            grade.updateGrade();

            // Recalculate student's GPA
            grade.getStudent().calculateGPA();

            System.out.println("Grade updated from " + oldValue + " to " + newValue +
                    " for student " + grade.getStudent().getUsername());
        } else {
            System.out.println("Professor does not have permission to update this grade");
        }
    }

    public void viewCourseGrades() {
        System.out.println("Course grades for professor: " + getUsername());
        for (Course course : courses) {
            System.out.println("\nCourse: " + course.getCourseName());
            System.out.println("Enrolled students and their grades:");

            for (Enrollment enrollment : course.getEnrollments()) {
                if (enrollment.getStatus() == EnrollmentStatus.ACTIVE) {
                    Student student = enrollment.getStudent();
                    System.out.print("Student: " + student.getUsername() + " - ");

                    // Find grade for this course
                    boolean gradeFound = false;
                    for (Grade grade : student.getGrades()) {
                        if (grade.getCourse().equals(course)) {
                            System.out.println("Grade: " + grade.getLetterGrade() +
                                    " (" + grade.getValue() + ")");
                            gradeFound = true;
                            break;
                        }
                    }
                    if (!gradeFound) {
                        System.out.println("No grade assigned");
                    }
                }
            }
        }
    }

    public boolean teachesCourse(String courseID) {
        return courses.stream()
                .anyMatch(course -> course.getCourseID().equals(courseID));
    }

    public List<Student> getAllStudents() {
        List<Student> allStudents = new ArrayList<>();
        for (Course course : courses) {
            for (Enrollment enrollment : course.getEnrollments()) {
                if (enrollment.getStatus() == EnrollmentStatus.ACTIVE) {
                    allStudents.add(enrollment.getStudent());
                }
            }
        }
        return allStudents;
    }

    // --- Getters and Setters ---

    public String getProfessorID() { return professorID; }
    public void setProfessorID(String professorID) { this.professorID = professorID; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getOfficeLocation() { return officeLocation; }
    public void setOfficeLocation(String officeLocation) { this.officeLocation = officeLocation; }

    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
}