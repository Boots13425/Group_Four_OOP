package com.university.gradessystem.service;

import com.university.gradessystem.model.SystemConfig;
import com.university.gradessystem.repository.SystemConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;

    public SystemConfigService(SystemConfigRepository systemConfigRepository) {
        this.systemConfigRepository = systemConfigRepository;
    }

    public Optional<SystemConfig> getSystemConfig() {
        return systemConfigRepository.findAll().stream().findFirst();
    }

    @Transactional
    public SystemConfig saveSystemConfig(SystemConfig config) {
        return systemConfigRepository.save(config);
    }

    @Transactional
    public SystemConfig getOrCreateSystemConfig() {
        return getSystemConfig().orElseGet(() -> {
            SystemConfig newConfig = new SystemConfig();
            newConfig.setUniversityName("University");
            newConfig.setAcademicYear("2023-2024");
            newConfig.setCurrentSemester("Spring");
            return systemConfigRepository.save(newConfig);
        });
    }

    @Transactional
    public void updateGeneralSettings(String universityName, String academicYear, String currentSemester) {
        SystemConfig config = getOrCreateSystemConfig();
        config.setUniversityName(universityName);
        config.setAcademicYear(academicYear);
        config.setCurrentSemester(currentSemester);
        systemConfigRepository.save(config);
    }

    @Transactional
    public void updateSecuritySettings(boolean requireUppercase, boolean requireLowercase,
            boolean requireNumbers, boolean requireSpecialChars,
            int minimumLength, int sessionTimeoutMinutes,
            int maxFailedLogins) {
        SystemConfig config = getOrCreateSystemConfig();
        config.setRequireUppercase(requireUppercase);
        config.setRequireLowercase(requireLowercase);
        config.setRequireNumbers(requireNumbers);
        config.setRequireSpecialChars(requireSpecialChars);
        config.setMinimumLength(minimumLength);
        config.setSessionTimeoutMinutes(sessionTimeoutMinutes);
        config.setMaxFailedLogins(maxFailedLogins);
        systemConfigRepository.save(config);
    }

    @Transactional
    public void updateBackupSettings(String backupFrequency, String backupTime, int retentionPeriodDays) {
        SystemConfig config = getOrCreateSystemConfig();
        config.setBackupFrequency(backupFrequency);

        // Parse time string (HH:mm) to LocalTime
        String[] timeParts = backupTime.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        config.setBackupTime(java.time.LocalTime.of(hour, minute));

        config.setRetentionPeriodDays(retentionPeriodDays);
        systemConfigRepository.save(config);
    }
}
