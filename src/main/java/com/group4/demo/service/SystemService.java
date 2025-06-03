package com.group4.demo.service;

import com.group4.demo.model.Grade;
import com.group4.demo.model.Student;
import com.group4.demo.model.SystemConfig;
import com.group4.demo.repository.GradeRepository;
import com.group4.demo.repository.StudentRepository;
import com.group4.demo.repository.SystemConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * System Service - Handles automated system operations
 * This implements the system use cases: Calculate GPA, Send notifications, Backup data
 */
@Service
public class SystemService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private GradeRepository gradeRepository;
    
    @Autowired
    private SystemConfigRepository systemConfigRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * CALCULATE GPA SCENARIO (Automated)
     * This runs automatically when grades are updated
     */
    public void recalculateAllStudentGPAs() {
        try {
            List<Student> students = studentRepository.findAll();
            
            for (Student student : students) {
                student.calculateGPA();
                studentRepository.save(student);
            }
            
            System.out.println("GPA recalculation completed for " + students.size() + " students");
            
        } catch (Exception e) {
            System.err.println("Error during GPA recalculation: " + e.getMessage());
            notificationService.sendSystemAlert("GPA Recalculation Error", e.getMessage());
        }
    }
    
    /**
     * BACKUP DATA SCENARIO (Automated)
     * This runs daily at the configured backup time
     */
    @Scheduled(cron = "0 0 2 * * *") // Default: 2:00 AM daily
    public void performDailyBackup() {
        try {
            SystemConfig config = systemConfigRepository.findCurrentConfig()
                .orElse(new SystemConfig());
            
            // Create backup directory if it doesn't exist
            File backupDir = new File("./backups");
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }
            
            // Generate backup filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String backupFileName = "backup_" + timestamp + ".sql";
            File backupFile = new File(backupDir, backupFileName);
            
            // Perform backup (simplified version - in production, use proper database backup tools)
            performDatabaseBackup(backupFile);
            
            // Update last backup time
            config.recordBackup();
            systemConfigRepository.save(config);
            
            System.out.println("Daily backup completed: " + backupFileName);
            
        } catch (Exception e) {
            System.err.println("Error during daily backup: " + e.getMessage());
            notificationService.sendSystemAlert("Backup Error", e.getMessage());
        }
    }
    
    /**
     * SEND GRADE NOTIFICATIONS SCENARIO (Automated)
     * This is triggered when grades are uploaded/updated
     */
    public void sendGradeNotifications(List<Grade> newGrades) {
        try {
            for (Grade grade : newGrades) {
                notificationService.sendGradeNotification(grade.getStudent(), grade);
            }
            
            System.out.println("Grade notifications sent for " + newGrades.size() + " grades");
            
        } catch (Exception e) {
            System.err.println("Error sending grade notifications: " + e.getMessage());
            notificationService.sendSystemAlert("Notification Error", e.getMessage());
        }
    }
    
    /**
     * SYSTEM HEALTH CHECK (Automated)
     * This runs periodically to check system health
     */
    @Scheduled(fixedRate = 3600000) // Every hour
    public void performSystemHealthCheck() {
        try {
            // Check database connectivity
            long studentCount = studentRepository.count();
            long gradeCount = gradeRepository.count();
            
            // Check for any system issues
            if (studentCount == 0) {
                notificationService.sendSystemAlert("System Warning", "No students found in database");
            }
            
            // Log system status
            System.out.println("System health check completed. Students: " + studentCount + ", Grades: " + gradeCount);
            
        } catch (Exception e) {
            System.err.println("Error during system health check: " + e.getMessage());
            notificationService.sendSystemAlert("System Health Check Error", e.getMessage());
        }
    }
    
    /**
     * CLEANUP OLD DATA (Automated)
     * This runs weekly to clean up old logs and temporary data
     */
    @Scheduled(cron = "0 0 3 * * SUN") // 3:00 AM every Sunday
    public void performWeeklyCleanup() {
        try {
            // Clean up old backup files (keep only last 30 days)
            cleanupOldBackups();
            
            // Clean up temporary files
            cleanupTempFiles();
            
            System.out.println("Weekly cleanup completed");
            
        } catch (Exception e) {
            System.err.println("Error during weekly cleanup: " + e.getMessage());
            notificationService.sendSystemAlert("Cleanup Error", e.getMessage());
        }
    }
    
    /**
     * Helper method to perform database backup
     */
    private void performDatabaseBackup(File backupFile) throws IOException {
        // This is a simplified backup - in production, use mysqldump or similar tools
        try (FileWriter writer = new FileWriter(backupFile)) {
            writer.write("-- University Grade Management System Backup\n");
            writer.write("-- Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n");
            
            // In a real system, you would use proper database backup commands
            // For demonstration, we'll write a simple backup header
            writer.write("-- This is a simplified backup file\n");
            writer.write("-- In production, use mysqldump or similar database-specific tools\n");
            writer.write("-- Example: mysqldump -u username -p university_grades > backup.sql\n\n");
            
            // Add basic statistics to backup file
            long studentCount = studentRepository.count();
            long gradeCount = gradeRepository.count();
            
            writer.write("-- Backup Statistics:\n");
            writer.write("-- Total Students: " + studentCount + "\n");
            writer.write("-- Total Grades: " + gradeCount + "\n");
            writer.write("-- Backup completed successfully\n");
        }
    }
    
    /**
     * Helper method to cleanup old backup files
     */
    private void cleanupOldBackups() {
        File backupDir = new File("./backups");
        if (backupDir.exists() && backupDir.isDirectory()) {
            File[] backupFiles = backupDir.listFiles((dir, name) -> name.startsWith("backup_") && name.endsWith(".sql"));
            
            if (backupFiles != null) {
                long thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);
                
                for (File file : backupFiles) {
                    if (file.lastModified() < thirtyDaysAgo) {
                        if (file.delete()) {
                            System.out.println("Deleted old backup file: " + file.getName());
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Helper method to cleanup temporary files
     */
    private void cleanupTempFiles() {
        File tempDir = new File("./temp");
        if (tempDir.exists() && tempDir.isDirectory()) {
            File[] tempFiles = tempDir.listFiles();
            
            if (tempFiles != null) {
                for (File file : tempFiles) {
                    if (file.delete()) {
                        System.out.println("Deleted temp file: " + file.getName());
                    }
                }
            }
        }
    }
    
    /**
     * Manual backup trigger for administrators
     */
    public String performManualBackup() {
        try {
            File backupDir = new File("./backups");
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String backupFileName = "manual_backup_" + timestamp + ".sql";
            File backupFile = new File(backupDir, backupFileName);
            
            performDatabaseBackup(backupFile);
            
            SystemConfig config = systemConfigRepository.findCurrentConfig()
                .orElse(new SystemConfig());
            config.recordBackup();
            systemConfigRepository.save(config);
            
            return "Manual backup completed successfully: " + backupFileName;
            
        } catch (Exception e) {
            String errorMessage = "Error performing manual backup: " + e.getMessage();
            notificationService.sendSystemAlert("Manual Backup Error", errorMessage);
            return errorMessage;
        }
    }
    
    /**
     * Get system statistics for dashboard
     */
    public SystemStatistics getSystemStatistics() {
        try {
            long totalStudents = studentRepository.count();
            long totalGrades = gradeRepository.count();
            
            // Calculate average GPA across all students
            List<Student> students = studentRepository.findAll();
            double totalGPA = students.stream()
                .mapToDouble(Student::getCurrentGPA)
                .filter(gpa -> gpa > 0)
                .average()
                .orElse(0.0);
            
            SystemConfig config = systemConfigRepository.findCurrentConfig()
                .orElse(new SystemConfig());
            
            return new SystemStatistics(
                totalStudents,
                totalGrades,
                totalGPA,
                config.getLastBackup(),
                "System statistics retrieved successfully"
            );
            
        } catch (Exception e) {
            return new SystemStatistics(0, 0, 0.0, null, "Error retrieving system statistics: " + e.getMessage());
        }
    }
    
    /**
     * System Statistics response class
     */
    public static class SystemStatistics {
        private long totalStudents;
        private long totalGrades;
        private double averageGPA;
        private LocalDateTime lastBackup;
        private String message;
        
        public SystemStatistics(long totalStudents, long totalGrades, double averageGPA, 
                              LocalDateTime lastBackup, String message) {
            this.totalStudents = totalStudents;
            this.totalGrades = totalGrades;
            this.averageGPA = averageGPA;
            this.lastBackup = lastBackup;
            this.message = message;
        }
        
        // Getters
        public long getTotalStudents() { return totalStudents; }
        public long getTotalGrades() { return totalGrades; }
        public double getAverageGPA() { return averageGPA; }
        public LocalDateTime getLastBackup() { return lastBackup; }
        public String getMessage() { return message; }
    }
}
