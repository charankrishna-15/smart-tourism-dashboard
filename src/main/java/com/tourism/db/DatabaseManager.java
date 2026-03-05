package com.tourism.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Manages the H2 embedded database connection and schema initialization.
 */
public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    // H2 file-based DB persisted to disk in the project folder
    private static final String DB_URL  = "jdbc:h2:./tourism_db;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "";

    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            logger.info("Connected to H2 database: {}", DB_URL);
            initializeSchema();
        } catch (Exception e) {
            logger.error("Failed to initialize database", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void initializeSchema() throws SQLException {
        logger.info("Initializing database schema...");
        try (Statement stmt = connection.createStatement()) {

            // ── Tourists table ──────────────────────────────────────────
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS tourists (
                    id                  INT AUTO_INCREMENT PRIMARY KEY,
                    name                VARCHAR(100) NOT NULL,
                    email               VARCHAR(150) UNIQUE NOT NULL,
                    preferred_climate   VARCHAR(30),
                    travel_style        VARCHAR(30),
                    budget              DOUBLE,
                    max_travel_days     INT,
                    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // ── Destinations table ──────────────────────────────────────
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS destinations (
                    id                  INT AUTO_INCREMENT PRIMARY KEY,
                    name                VARCHAR(100) NOT NULL,
                    country             VARCHAR(80)  NOT NULL,
                    climate             VARCHAR(30),
                    travel_style        VARCHAR(30),
                    avg_rating          DOUBLE DEFAULT 0.0,
                    total_reviews       INT    DEFAULT 0,
                    avg_cost_per_day    DOUBLE DEFAULT 0.0,
                    best_season_start   INT,
                    best_season_end     INT,
                    latitude            DOUBLE,
                    longitude           DOUBLE,
                    description         VARCHAR(500),
                    activities          VARCHAR(300)
                )
            """);

            // ── Seasonal data table ─────────────────────────────────────
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS seasonal_data (
                    id                      INT AUTO_INCREMENT PRIMARY KEY,
                    destination_id          INT  NOT NULL,
                    visit_year              INT  NOT NULL,
                    visit_month             INT  NOT NULL,
                    visitor_count           INT  DEFAULT 0,
                    avg_temperature_celsius DOUBLE,
                    avg_precipitation_mm    DOUBLE,
                    FOREIGN KEY (destination_id) REFERENCES destinations(id)
                )
            """);

            // ── Itineraries table ───────────────────────────────────────
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS itineraries (
                    id                  INT AUTO_INCREMENT PRIMARY KEY,
                    tourist_id          INT NOT NULL,
                    start_date          DATE,
                    end_date            DATE,
                    total_cost          DOUBLE,
                    total_distance_km   DOUBLE,
                    total_days          INT,
                    optimization_score  DOUBLE,
                    notes               VARCHAR(500),
                    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (tourist_id) REFERENCES tourists(id)
                )
            """);

            // ── Itinerary stops table ───────────────────────────────────
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS itinerary_stops (
                    id              INT AUTO_INCREMENT PRIMARY KEY,
                    itinerary_id    INT NOT NULL,
                    destination_id  INT NOT NULL,
                    stop_order      INT NOT NULL,
                    days_spent      INT NOT NULL,
                    cost_for_stop   DOUBLE,
                    FOREIGN KEY (itinerary_id)   REFERENCES itineraries(id),
                    FOREIGN KEY (destination_id) REFERENCES destinations(id)
                )
            """);

            logger.info("Schema initialized successfully.");
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Database connection closed.");
            }
        } catch (SQLException e) {
            logger.error("Error closing database connection", e);
        }
    }
}
