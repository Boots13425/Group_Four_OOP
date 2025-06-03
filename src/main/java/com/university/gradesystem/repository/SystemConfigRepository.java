package com.university.gradesystem.repository;

import com.university.gradesystem.model.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
    Optional<SystemConfig> findByConfigID(Long configID);
    Optional<SystemConfig> findByBackupSchedule(String backupSchedule);
}