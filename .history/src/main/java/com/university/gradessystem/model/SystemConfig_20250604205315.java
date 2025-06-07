package com.university.gradessystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "system_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
