package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application E-Commerce Naive - USAGE EDUCATIF UNIQUEMENT
 * Contient intentionnellement des mauvaises pratiques pour TP SonarQube
 */
@SpringBootApplication
public class EcommerceApplication {

    // MAUVAISE PRATIQUE: Configuration en dur
    public static final String APP_VERSION = "1.0.0";

    public static void main(String[] args) {
        // MAUVAISE PRATIQUE: System.out au lieu de Logger
        System.out.println("=================================");
        System.out.println("Demarrage E-Commerce Application");
        System.out.println("Version: " + APP_VERSION);
        System.out.println("=================================");

        SpringApplication.run(EcommerceApplication.class, args);

        System.out.println("Application demarree sur http://localhost:8080");
        System.out.println("Console H2: http://localhost:8080/h2-console");
    }
}
