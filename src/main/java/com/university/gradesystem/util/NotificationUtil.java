package com.university.gradesystem.util;

public class NotificationUtil {
    public static void sendNotification(String recipient, String subject, String message) {
        System.out.printf("Notification sent to %s | Subject: %s | Message: %s%n", recipient, subject, message);
    }
}