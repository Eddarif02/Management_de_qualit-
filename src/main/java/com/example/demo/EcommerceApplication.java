package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application E-Commerce - Version corrigée pour SonarQube
 */
@SpringBootApplication
public class EcommerceApplication {

    private static final Logger log = LoggerFactory.getLogger(EcommerceApplication.class);

    public static void main(String[] args) {
        log.info("====================================");
        log.info("Démarrage de l'application E-Commerce");
        log.info("====================================");

        SpringApplication.run(EcommerceApplication.class, args);

        log.info("Application prête sur http://localhost:8080");
        log.info("Console H2: http://localhost:8080/h2-console");
    }
}
