package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application E-Commerce - TP SonarQube
 * CODE AVEC MAUVAISES PRATIQUES INTENTIONNELLES - USAGE EDUCATIF UNIQUEMENT
 */
@SpringBootApplication
public class EcommerceApplication {

    // MAUVAISE PRATIQUE: Constante inutilisée
    public static final String VERSION = "1.0.0";
    public static final String AUTHOR = "Dev Junior";

    public static void main(String[] args) {
        // MAUVAISE PRATIQUE: System.out au lieu de Logger
        System.out.println("====================================");
        System.out.println("Demarrage de l'application E-Commerce");
        System.out.println("====================================");

        SpringApplication.run(EcommerceApplication.class, args);

        System.out.println("Application prete sur http://localhost:8080");
        System.out.println("Console H2: http://localhost:8080/h2-console");
    }
}
