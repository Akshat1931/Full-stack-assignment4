package com.example.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Library Management System.
 * 
 * The @SpringBootApplication annotation encapsulates:
 * 1. @SpringBootConfiguration: Indicates that the class provides Spring Boot application configuration.
 * 2. @EnableAutoConfiguration: Instructs Spring Boot to automatically configure beans based on classpath dependencies.
 * 3. @ComponentScan: Scans for Spring components (@Component, @Controller, @Service, @Repository) in this package and subpackages.
 */
@SpringBootApplication
public class LibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }
}
