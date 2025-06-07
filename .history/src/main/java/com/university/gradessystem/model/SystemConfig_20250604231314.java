package com.university.gradessystem.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "system_config")
public class SystemConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String universityName;

    private String academicYear;

    private String currentSemester;

    // Password policy
    private boolean requireUppercase = true;
    private boolean requireLowercase = true;
    private boolean requireNumbers = true;
    private boolean requireSpecialChars = true;
    private int minimumLength = 8;

    // Session settings
    private int sessionTimeoutMinutes = 30;
    private int maxFailedLogins = 5;

    // Backup settings
    private String backupFrequency = "DAILY"; // DAILY, WEEKLY, MONTHLY
    private LocalTime backupTime = LocalTime.of(2, 0); // 2:00 AM
    private int retentionPeriodDays = 30;

    // Constructors
    public SystemConfig() {
    }

    public SystemConfig(Long id, String universityName, String academicYear, String currentSemester,
            boolean requireUppercase, boolean requireLowercase, boolean requireNumbers,
            boolean requireSpecialChars, int minimumLength, int sessionTimeoutMinutes,
            int maxFailedLogins, String backupFrequency, LocalTime backupTime,
            int retentionPeriodDays) {
        this.id = id;
        this.universityName = universityName;
        this.academicYear = academicYear;
        this.currentSemester = currentSemester;
        this.requireUppercase = requireUppercase;
        this.requireLowercase = requireLowercase;
        this.requireNumbers = requireNumbers;
        this.requireSpecialChars = requireSpecialChars;
        this.minimumLength = minimumLength;
        this.sessionTimeoutMinutes = sessionTimeoutMinutes;
        this.maxFailedLogins = maxFailedLogins;
        this.backupFrequency = backupFrequency;
        this.backupTime = backupTime;
        this.retentionPeriodDays = retentionPeriodDays;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getCurrentSemester() {
        return currentSemester;
    }

    public void setCurrentSemester(String currentSemester) {
        this.currentSemester = currentSemester;
    }

    public boolean isRequireUppercase() {
        return requireUppercase;
    }

    public void setRequireUppercase(boolean requireUppercase) {
        this.requireUppercase = requireUppercase;
    }

    public boolean isRequireLowercase() {
        return requireLowercase;
    }

    public void setRequireLowercase(boolean requireLowercase) {
        this.requireLowercase = requireLowercase;
    }

    public boolean isRequireNumbers() {
        return requireNumbers;
    }

    public void setRequireNumbers(boolean requireNumbers) {
        this.requireNumbers = requireNumbers;
    }

    public boolean isRequireSpecialChars() {
        return requireSpecialChars;
    }

    public void setRequireSpecialChars(boolean requireSpecialChars) {
        this.requireSpecialChars = requireSpecialChars;
    }

    public int getMinimumLength() {
        return minimumLength;
    }

    public void setMinimumLength(int minimumLength) {
        this.minimumLength = minimumLength;
    }

    public int getSessionTimeoutMinutes() {
        return sessionTimeoutMinutes;
    }

    public void setSessionTimeoutMinutes(int sessionTimeoutMinutes) {
        this.sessionTimeoutMinutes = sessionTimeoutMinutes;
    }

    public int getMaxFailedLogins() {
        return maxFailedLogins;
    }

    public void setMaxFailedLogins(int maxFailedLogins) {
        this.maxFailedLogins = maxFailedLogins;
    }

    public String getBackupFrequency() {
        return backupFrequency;
    }

    public void setBackupFrequency(String backupFrequency) {
        this.backupFrequency = backupFrequency;
    }

    public LocalTime getBackupTime() {
        return backupTime;
    }

    public void setBackupTime(LocalTime backupTime) {
        this.backupTime = backupTime;
    }

    public int getRetentionPeriodDays() {
        return retentionPeriodDays;
    }

    public void setRetentionPeriodDays(int retentionPeriodDays) {
        this.retentionPeriodDays = retentionPeriodDays;
    }
}
