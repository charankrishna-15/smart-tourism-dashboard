package com.tourism.main;

import com.tourism.db.DatabaseManager;
import com.tourism.service.DataSeeder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import jakarta.annotation.PostConstruct;

/**
 * Smart Tourism Recommendation & Planning System
 * Spring Boot entry point — exposes REST API on port 8080.
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.tourism")
public class TourismApp {
    private static final Logger logger = LoggerFactory.getLogger(TourismApp.class);

    public static void main(String[] args) {
        SpringApplication.run(TourismApp.class, args);
    }

    @PostConstruct
    public void init() {
        try {
            logger.info("Initializing database and seeding data...");
            DatabaseManager.getInstance();
            new DataSeeder().seed();
            logger.info("Database ready.");
        } catch (Exception e) {
            logger.error("Failed to initialize database", e);
        }
    }
}
