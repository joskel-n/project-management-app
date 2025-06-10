package com.projectmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Project Management application.
 * The @SpringBootApplication annotation enables auto-configuration,
 * component scanning, and defines this as the main configuration class.
 */
@SpringBootApplication
public class ProjectManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectManagementApplication.class, args);
    }
}