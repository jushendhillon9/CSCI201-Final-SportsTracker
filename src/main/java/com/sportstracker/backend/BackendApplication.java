package com.sportstracker.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot application.
 * Scans both our backend package and the imported CFBD API package from Adl's branch.
 */
@SpringBootApplication(
        scanBasePackages = {
                "com.sportstracker.backend",
                "com.example.cfbtracker"
        }
)
@EnableAsync
@EnableScheduling
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}