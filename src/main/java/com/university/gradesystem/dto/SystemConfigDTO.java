package com.university.gradesystem.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigDTO {
    private Long configID;
    private String backupSchedule;
    private String notificationSettings;
    private String systemParameters;
}