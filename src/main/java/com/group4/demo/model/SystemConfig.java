package com.group4.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * SystemConfig class - Handles system-wide settings
 * Adapted to match the provided SystemConfig.java structure
 */
@Entity
@Table(name = "system_config")
public class SystemConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "config_id", unique = true)
    private String configID;
    
    @Column(name = "backup_schedule")
    private String backupSchedule = "0 0 2 * * *"; // Daily at 2 AM
    
    @Column(name = "backup_time")
    private LocalTime backupTime = LocalTime.of(2, 0);
    
    // Store notification settings as JSON string in database
    @Column(name = "notification_setting", columnDefinition = "TEXT")
    private String notificationSettingJson;
    
    // Transient field for in-memory notification settings map
    @Transient
    private Map<String, Object> notificationSetting = new HashMap<>();
    
    // Store system parameters as JSON string in database
    @Column(name = "system_parameter", columnDefinition = "TEXT")
    private String systemParameterJson;
    
    // Transient field for in-memory system parameters map
    @Transient
    private Map<String, Object> systemParameter = new HashMap<>();
    
    @Column(name = "admin_email")
    private String adminEmail;
    
    @Column(name = "system_name")
    private String systemName = "University Grade Management System";
    
    @Column(name = "max_login_attempts")
    private Integer maxLoginAttempts = 3;
    
    @Column(name = "session_timeout_minutes")
    private Integer sessionTimeoutMinutes = 30;
    
    @Column(name = "grade_release_enabled")
    private boolean gradeReleaseEnabled = true;
    
    @Column(name = "enrollment_enabled")
    private boolean enrollmentEnabled = true;
    
    @Column(name = "last_backup")
    private LocalDateTime lastBackup;
    
    @Column(name = "last_modified")
    private LocalDateTime lastModified;
    
    @Column(name = "modified_by")
    private String modifiedBy;
    
    // Constructors
    public SystemConfig() {
        this.configID = generateConfigID();
        this.lastModified = LocalDateTime.now();
        initializeDefaultSettings();
    }
    
    public SystemConfig(String backupSchedule, String adminEmail) {
        this();
        this.backupSchedule = backupSchedule;
        this.adminEmail = adminEmail;
    }
    
    /**
     * Update configuration settings
     */
    public void updateConfig() {
        this.lastModified = LocalDateTime.now();
        System.out.println("System configuration updated at: " + lastModified);
    }
    
    public void updateConfig(String modifiedBy) {
        this.lastModified = LocalDateTime.now();
        this.modifiedBy = modifiedBy;
        System.out.println("System configuration updated by: " + modifiedBy + " at: " + lastModified);
    }
    
    /**
     * Backup data according to schedule
     */
    public void backupDate() {
        backupData();
    }
    
    public void backupData() {
        try {
            System.out.println("Starting system backup...");
            
            // Simulate backup process
            Thread.sleep(1000); // Simulate backup time
            
            this.lastBackup = LocalDateTime.now();
            System.out.println("System backup completed at: " + lastBackup);
            
        } catch (InterruptedException e) {
            System.err.println("Backup process interrupted: " + e.getMessage());
        }
    }
    
    /**
     * Send notification based on settings
     */
    public void sendNotification() {
        sendNotification("System notification", "Default system message");
    }
    
    public void sendNotification(String subject, String message) {
        if (isNotificationEnabled("emailEnabled")) {
            System.out.println("Sending email notification:");
            System.out.println("To: " + adminEmail);
            System.out.println("Subject: " + subject);
            System.out.println("Message: " + message);
            System.out.println("Sent at: " + LocalDateTime.now());
        } else {
            System.out.println("Email notifications are disabled");
        }
    }
    
    /**
     * Check if it's time for backup
     */
    public boolean isBackupTime() {
        LocalTime now = LocalTime.now();
        return now.isAfter(backupTime) && now.isBefore(backupTime.plusMinutes(30));
    }
    
    /**
     * Record successful backup
     */
    public void recordBackup() {
        this.lastBackup = LocalDateTime.now();
        updateConfig("System");
    }
    
    /**
     * Check if specific notification is enabled
     */
    public boolean isNotificationEnabled(String notificationType) {
        Object setting = notificationSetting.get(notificationType);
        return setting instanceof Boolean ? (Boolean) setting : false;
    }
    
    /**
     * Update notification setting
     */
    public void updateNotificationSetting(String key, Object value) {
        notificationSetting.put(key, value);
        System.out.println("Notification setting updated: " + key + " = " + value);
    }
    
    /**
     * Update system parameter
     */
    public void updateSystemParameter(String key, Object value) {
        systemParameter.put(key, value);
        System.out.println("System parameter updated: " + key + " = " + value);
    }
    
    /**
     * Get system parameter value
     */
    public Object getSystemParameter(String key) {
        return systemParameter.getOrDefault(key, null);
    }
    
    /**
     * Initialize default settings
     */
    private void initializeDefaultSettings() {
        // Default notification settings
        notificationSetting.put("emailEnabled", true);
        notificationSetting.put("gradeNotifications", true);
        notificationSetting.put("enrollmentNotifications", true);
        notificationSetting.put("systemAlerts", true);
        
        // Default system parameters
        systemParameter.put("maxFileSize", "10MB");
        systemParameter.put("sessionTimeout", 30);
        systemParameter.put("passwordMinLength", 8);
        systemParameter.put("backupRetentionDays", 30);
    }
    
    /**
     * Generate unique config ID
     */
    private String generateConfigID() {
        return "CFG" + System.currentTimeMillis();
    }
    
    /**
     * Get default notification settings
     */
    public static String getDefaultNotificationSettings() {
        return "{\"emailEnabled\":true,\"gradeNotifications\":true,\"enrollmentNotifications\":true,\"systemAlerts\":true}";
    }
    
    /**
     * Display configuration information
     */
    public void displayConfigInfo() {
        System.out.println("=== System Configuration ===");
        System.out.println("Config ID: " + configID);
        System.out.println("System Name: " + systemName);
        System.out.println("Admin Email: " + adminEmail);
        System.out.println("Backup Schedule: " + backupSchedule);
        System.out.println("Backup Time: " + backupTime);
        System.out.println("Last Backup: " + (lastBackup != null ? lastBackup : "Never"));
        System.out.println("Max Login Attempts: " + maxLoginAttempts);
        System.out.println("Session Timeout: " + sessionTimeoutMinutes + " minutes");
        System.out.println("Grade Release Enabled: " + gradeReleaseEnabled);
        System.out.println("Enrollment Enabled: " + enrollmentEnabled);
        System.out.println("Last Modified: " + lastModified);
        System.out.println("Modified By: " + (modifiedBy != null ? modifiedBy : "System"));
    }
    
    /**
     * Validate configuration settings
     */
    public boolean validateConfig() {
        boolean isValid = true;
        
        if (adminEmail == null || adminEmail.isEmpty()) {
            System.out.println("Warning: Admin email is not set");
            isValid = false;
        }
        
        if (maxLoginAttempts <= 0) {
            System.out.println("Warning: Invalid max login attempts value");
            isValid = false;
        }
        
        if (sessionTimeoutMinutes <= 0) {
            System.out.println("Warning: Invalid session timeout value");
            isValid = false;
        }
        
        return isValid;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getConfigID() { return configID; }
    public void setConfigID(String configID) { this.configID = configID; }
    
    public String getBackupSchedule() { return backupSchedule; }
    public void setBackupSchedule(String backupSchedule) { this.backupSchedule = backupSchedule; }
    
    public LocalTime getBackupTime() { return backupTime; }
    public void setBackupTime(LocalTime backupTime) { this.backupTime = backupTime; }
    
    public Map<String, Object> getNotificationSetting() { return notificationSetting; }
    public void setNotificationSetting(Map<String, Object> notificationSetting) { this.notificationSetting = notificationSetting; }
    
    public String getNotificationSettingJson() { return notificationSettingJson; }
    public void setNotificationSettingJson(String notificationSettingJson) { this.notificationSettingJson = notificationSettingJson; }
    
    public Map<String, Object> getSystemParameter() { return systemParameter; }
    public void setSystemParameter(Map<String, Object> systemParameter) { this.systemParameter = systemParameter; }
    
    public String getSystemParameterJson() { return systemParameterJson; }
    public void setSystemParameterJson(String systemParameterJson) { this.systemParameterJson = systemParameterJson; }
    
    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
    
    public String getSystemName() { return systemName; }
    public void setSystemName(String systemName) { this.systemName = systemName; }
    
    public Integer getMaxLoginAttempts() { return maxLoginAttempts; }
    public void setMaxLoginAttempts(Integer maxLoginAttempts) { this.maxLoginAttempts = maxLoginAttempts; }
    
    public Integer getSessionTimeoutMinutes() { return sessionTimeoutMinutes; }
    public void setSessionTimeoutMinutes(Integer sessionTimeoutMinutes) { this.sessionTimeoutMinutes = sessionTimeoutMinutes; }
    
    public boolean isGradeReleaseEnabled() { return gradeReleaseEnabled; }
    public void setGradeReleaseEnabled(boolean gradeReleaseEnabled) { this.gradeReleaseEnabled = gradeReleaseEnabled; }
    
    public boolean isEnrollmentEnabled() { return enrollmentEnabled; }
    public void setEnrollmentEnabled(boolean enrollmentEnabled) { this.enrollmentEnabled = enrollmentEnabled; }
    
    public LocalDateTime getLastBackup() { return lastBackup; }
    public void setLastBackup(LocalDateTime lastBackup) { this.lastBackup = lastBackup; }
    
    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }
    
    public String getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(String modifiedBy) { this.modifiedBy = modifiedBy; }
}
