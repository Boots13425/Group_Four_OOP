package com.university.gradessystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GradesSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(GradesSystemApplication.class, args);
    }
}
