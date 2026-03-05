# Smart Tourism Recommendation & Planning System

A Java-based intelligent tourism planner implementing all 5 modules described in the problem statement.

## Architecture

```
src/main/java/com/tourism/
├── model/          # Entity classes
│   ├── Tourist.java
│   ├── Destination.java
│   ├── Itinerary.java
│   ├── ItineraryStop.java
│   ├── SeasonalData.java
│   └── Recommendation.java
├── db/             # DATABASE MODULE — H2 data access layer
│   ├── DatabaseManager.java
│   ├── DestinationDAO.java
│   ├── TouristDAO.java
│   ├── SeasonalDataDAO.java
│   └── ItineraryDAO.java
├── service/        # CORE LOGIC MODULES
│   ├── RecommendationService.java  # K-Means++ clustering + similarity
│   ├── ForecastService.java        # Linear regression seasonal forecasting
│   ├── OptimizationService.java    # TSP + budget-constrained itinerary
│   └── DataSeeder.java             # Sample data population
├── report/         # REPORTING MODULE
│   └── ReportGenerator.java        # JFreeChart visual reports
└── main/
    └── TourismApp.java             # Entry point
```

## Prerequisites

1. **Java JDK 11+** — Download from https://adoptium.net/
2. **Apache Maven 3.8+** — Download from https://maven.apache.org/download.cgi

## Quick Start

```bash
# 1. Install dependencies and compile
mvn clean package

# 2. Run the application
java -jar target/smart-tourism-jar-with-dependencies.jar
```

Or directly via Maven:
```bash
mvn exec:java -Dexec.mainClass="com.tourism.main.TourismApp"
```

## Modules

| # | Module | Technology | Description |
|---|--------|-----------|-------------|
| 1 | Destination DB | H2 + JDBC | Store destinations with ratings, climate, coordinates |
| 2 | Recommendation | K-Means++ (Apache Commons Math) | Cluster destinations, compute similarity scores |
| 3 | Forecast Engine | Linear Regression + Seasonal Index | Predict monthly visitor counts |
| 4 | Optimization | TSP Nearest-Neighbor + Greedy | Minimize travel distance and cost |
| 5 | Reporting | JFreeChart | Generate PNG charts + text reports |

## Output

After running, check:
- **Console** — full module-by-module output
- **`./reports/`** — generated PNG charts
- **`tourism_system.log`** — detailed application log
- **`tourism_db.*`** — H2 persistent database files
