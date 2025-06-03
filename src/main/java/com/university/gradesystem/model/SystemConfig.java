package com.university.gradesystem.model;

import jakarta.persistence.*;

@Entity
public class SystemConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long configID;
    private String backupSchedule;
    private String notificationSettings;
    private String systemParameters;

    public SystemConfig() {}

    public Long getConfigID() {
        return configID;
    }

    public void setConfigID(Long configID) {
        this.configID = configID;
    }

    public String getBackupSchedule() {
        return backupSchedule;
    }

    public void setBackupSchedule(String backupSchedule) {
        this.backupSchedule = backupSchedule;
    }

    public String getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(String notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    public String getSystemParameters() {
        return systemParameters;
    }

    public void setSystemParameters(String systemParameters) {
        this.systemParameters = systemParameters;
    }
}