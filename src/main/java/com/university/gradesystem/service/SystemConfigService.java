package com.university.gradesystem.service;

import com.university.gradesystem.model.SystemConfig;
import com.university.gradesystem.repository.SystemConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SystemConfigService {
    private final SystemConfigRepository configRepo;

    public SystemConfigService(SystemConfigRepository configRepo) {
        this.configRepo = configRepo;
    }

    public List<SystemConfig> getAll() { return configRepo.findAll(); }

    public Optional<SystemConfig> getById(Long id) { return configRepo.findById(id); }

    public SystemConfig save(SystemConfig config) { return configRepo.save(config); }

    public void delete(Long id) { configRepo.deleteById(id); }

    // Update config settings
    public SystemConfig updateConfig(Long configId, String backupSchedule, String notificationSettings, String systemParameters) {
        SystemConfig config = configRepo.findById(configId).orElseThrow();
        config.setBackupSchedule(backupSchedule);
        config.setNotificationSettings(notificationSettings);
        config.setSystemParameters(systemParameters);
        return configRepo.save(config);
    }

    // Get backup date info
    public String backupDate(Long configId) {
        SystemConfig config = configRepo.findById(configId).orElseThrow();
        return "Next backup scheduled for: " + config.getBackupSchedule();
    }

    // Send notification
    public String sendNotification(Long configId, String message) {
        SystemConfig config = configRepo.findById(configId).orElseThrow();
        // Example: Just returns a string for demonstration
        return "Notification sent (" + config.getNotificationSettings() + "): " + message;
    }
}