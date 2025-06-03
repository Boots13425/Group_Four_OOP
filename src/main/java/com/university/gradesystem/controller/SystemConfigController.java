package com.university.gradesystem.controller;

import com.university.gradesystem.model.SystemConfig;
import com.university.gradesystem.service.SystemConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/systemconfigs")
public class SystemConfigController {
    private final SystemConfigService systemConfigService;
    public SystemConfigController(SystemConfigService systemConfigService) { this.systemConfigService = systemConfigService; }

    @GetMapping
    public List<SystemConfig> getAll() { return systemConfigService.getAll(); }

    @GetMapping("/{id}")
    public SystemConfig getById(@PathVariable Long id) { return systemConfigService.getById(id).orElse(null); }

    @PostMapping
    public SystemConfig create(@RequestBody SystemConfig config) { return systemConfigService.save(config); }

    @PutMapping("/{id}")
    public SystemConfig update(@PathVariable Long id, @RequestBody SystemConfig config) {
        config.setConfigID(id);
        return systemConfigService.save(config);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { systemConfigService.delete(id); }

    // Update config settings
    @PutMapping("/{configId}/update-config")
    public SystemConfig updateConfig(@PathVariable Long configId, @RequestParam String backupSchedule,
                                     @RequestParam String notificationSettings, @RequestParam String systemParameters) {
        return systemConfigService.updateConfig(configId, backupSchedule, notificationSettings, systemParameters);
    }

    // Get backup date info
    @GetMapping("/{configId}/backup-date")
    public String backupDate(@PathVariable Long configId) {
        return systemConfigService.backupDate(configId);
    }

    // Send notification
    @PostMapping("/{configId}/send-notification")
    public String sendNotification(@PathVariable Long configId, @RequestParam String message) {
        return systemConfigService.sendNotification(configId, message);
    }
}