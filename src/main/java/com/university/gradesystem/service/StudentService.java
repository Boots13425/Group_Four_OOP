package com.university.gradesystem.service;

import com.university.gradesystem.model.Enrollment;
import com.university.gradesystem.model.Grade;
import com.university.gradesystem.model.Student;
import com.university.gradesystem.model.Course;
import com.university.gradesystem.repository.StudentRepository;
import com.university.gradesystem.repository.EnrollmentRepository;
import com.university.gradesystem.repository.GradeRepository;
import com.university.gradesystem.repository.CourseRepository;
import com.university.gradesystem.util.CSVExporter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final GradeRepository gradeRepo;
    private final CourseRepository courseRepo;

    public StudentService(StudentRepository studentRepo, EnrollmentRepository enrollmentRepo,
                          GradeRepository gradeRepo, CourseRepository courseRepo) {
        this.studentRepo = studentRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.gradeRepo = gradeRepo;
        this.courseRepo = courseRepo;
    }

    public List<Student> getAll() { return studentRepo.findAll(); }

    public Optional<Student> getById(Long id) { return studentRepo.findById(id); }

    public Student save(Student student) { return studentRepo.save(student); }

    public void delete(Long id) { studentRepo.deleteById(id); }

    // --- Custom methods for controller ---

    // View all grades for a student
    public List<Grade> viewGrades(Long studentId) {
        return enrollmentRepo.findAll().stream()
                .filter(e -> e.getStudent().getUserID().equals(studentId))
                .flatMap(e -> gradeRepo.findAll().stream().filter(g -> g.getEnrollment().getEnrollmentID().equals(e.getEnrollmentID())))
                .collect(Collectors.toList());
    }

    // Export grades for a student as CSV
    public String exportGradeData(Long studentId) {
        return CSVExporter.gradesToCSV(viewGrades(studentId));
    }

    // View historical grades for a student for a semester
    public List<Grade> viewHistoricalGrades(Long studentId, String semester) {
        return enrollmentRepo.findAll().stream()
                .filter(e -> e.getStudent().getUserID().equals(studentId)
                        && e.getCourse().getSemester().equalsIgnoreCase(semester))
                .flatMap(e -> gradeRepo.findAll().stream().filter(g -> g.getEnrollment().getEnrollmentID().equals(e.getEnrollmentID())))
                .collect(Collectors.toList());
    }

    // Register student for a course
    public Enrollment registerCourse(Long studentId, Long courseId) {
        Student student = studentRepo.findById(studentId).orElseThrow();
        Course course = courseRepo.findById(courseId).orElseThrow();
        if (course.getCurrentEnrollment() < course.getMaxCapacity()) {
            Enrollment enrollment = new Enrollment();
            enrollment.setCourse(course);
            enrollment.setStudent(student);
            enrollment.setEnrollmentDate(java.time.LocalDate.now());
            enrollment.setStatus("ENROLLED");
            course.setCurrentEnrollment(course.getCurrentEnrollment() + 1);
            courseRepo.save(course);
            return enrollmentRepo.save(enrollment);
        } else {
            throw new RuntimeException("Course is full");
        }
    }
}