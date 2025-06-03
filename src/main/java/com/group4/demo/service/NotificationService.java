package com.group4.demo.service;

import com.group4.demo.model.Course;
import com.group4.demo.model.Grade;
import com.group4.demo.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Notification Service - Handles email notifications and system alerts
 * This supports the notification requirements from your use cases
 */
@Service
public class NotificationService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    /**
     * Send enrollment notification to student
     */
    public void sendEnrollmentNotification(Student student, Course course, String action) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(student.getEmail());
            message.setSubject("Course " + action.toUpperCase() + " Notification");
            
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "You have successfully %s %s the course:\n" +
                "Course Code: %s\n" +
                "Course Title: %s\n" +
                "Credits: %d\n\n" +
                "Date: %s\n\n" +
                "Best regards,\n" +
                "University Grade Management System",
                student.getUsername(),
                action,
                action.equals("enrolled") ? "in" : "from",
                course.getCourseID(),
                course.getCourseName(),
                course.getCredits(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
            
            message.setText(emailBody);
            mailSender.send(message);
            
        } catch (Exception e) {
            // Log error but don't fail the main operation
            System.err.println("Failed to send enrollment notification: " + e.getMessage());
        }
    }
    
    /**
     * Send grade notification to student
     */
    public void sendGradeNotification(Student student, Grade grade) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(student.getEmail());
            message.setSubject("New Grade Posted");
            
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "A new grade has been posted for your course:\n\n" +
                "Course: %s (%s)\n" +
                "Score: %.1f\n" +
                "Letter Grade: %s\n" +
                "Grade Points: %.1f\n" +
                "Evaluation: %s\n" +
                "Term: %s\n\n" +
                "You can view your complete transcript by logging into the system.\n\n" +
                "Best regards,\n" +
                "University Grade Management System",
                student.getUsername(),
                grade.getCourse().getCourseName(),
                grade.getCourse().getCourseID(),
                grade.getGradePoints(),
                grade.getLetterGrade(),
                grade.getGradePoints(),
                grade.getEvaluation(),
                grade.getTerm()
            );
            
            message.setText(emailBody);
            mailSender.send(message);
            
        } catch (Exception e) {
            System.err.println("Failed to send grade notification: " + e.getMessage());
        }
    }
    
    /**
     * Send verification email for user registration
     */
    public void sendVerificationEmail(String email, String subject, String messageBody) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(subject);
            message.setText(messageBody);
            
            mailSender.send(message);
            
        } catch (Exception e) {
            System.err.println("Failed to send verification email: " + e.getMessage());
        }
    }
    
    /**
     * Send welcome email after successful registration
     */
    public void sendWelcomeEmail(String email, String subject, String messageBody) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(subject);
            message.setText(messageBody);
            
            mailSender.send(message);
            
        } catch (Exception e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
    }
    
    /**
     * Send system alert to administrators
     */
    public void sendSystemAlert(String subject, String alertMessage) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("admin@university.edu"); // This would come from system config
            message.setSubject("System Alert: " + subject);
            message.setText(alertMessage + "\n\nGenerated at: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            mailSender.send(message);
            
        } catch (Exception e) {
            System.err.println("Failed to send system alert: " + e.getMessage());
        }
    }
    
    /**
     * Send password reset email
     */
    public void sendPasswordResetEmail(String email, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Password Reset Request");
            
            String emailBody = String.format(
                "Dear User,\n\n" +
                "You have requested to reset your password for the University Grade Management System.\n\n" +
                "Your password reset token is: %s\n\n" +
                "This token will expire in 1 hour.\n" +
                "If you didn't request this password reset, please ignore this email.\n\n" +
                "Best regards,\n" +
                "University Grade Management System",
                resetToken
            );
            
            message.setText(emailBody);
            mailSender.send(message);
            
        } catch (Exception e) {
            System.err.println("Failed to send password reset email: " + e.getMessage());
        }
    }
}
