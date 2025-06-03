package com.university.gradesystem.util;

import java.time.LocalDateTime;

public class BackupUtil {
    public static LocalDateTime performBackup() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("System backup performed at " + now);
        return now;
    }
}