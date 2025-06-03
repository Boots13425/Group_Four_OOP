package com.university;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Application Class
 * This is the entry point of our University Grade Management System
 * @EnableScheduling allows us to run automated tasks like backups
 */
@SpringBootApplication
@EnableScheduling
public class GradeManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(GradeManagementApplication.class, args);
        System.out.println("University Grade Management System Started Successfully!");
    }
}
