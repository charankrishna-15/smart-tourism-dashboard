package com.tourism.model;

/**
 * Represents a tourist destination with full attributes, ratings, and
 * geo-coordinates.
 */
public class Destination {
    private int id;
    private String name;
    private String country;
    private String climate; // "tropical", "temperate", "arctic", "arid"
    private String travelStyle; // "adventure", "cultural", "relaxation", "eco"
    private double avgRating; // 1.0 - 5.0
    private int totalReviews;
    private double avgCostPerDay; // INR per day
    private int bestSeasonStart; // 1-12 (month)
    private int bestSeasonEnd; // 1-12 (month)
    private double latitude;
    private double longitude;
    private String description;
    private String activities; // comma-separated

    public Destination() {
    }

    public Destination(int id, String name, String country, String climate,
            String travelStyle, double avgRating, int totalReviews,
            double avgCostPerDay, int bestSeasonStart, int bestSeasonEnd,
            double latitude, double longitude, String description, String activities) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.climate = climate;
        this.travelStyle = travelStyle;
        this.avgRating = avgRating;
        this.totalReviews = totalReviews;
        this.avgCostPerDay = avgCostPerDay;
        this.bestSeasonStart = bestSeasonStart;
        this.bestSeasonEnd = bestSeasonEnd;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.activities = activities;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getClimate() {
        return climate;
    }

    public void setClimate(String climate) {
        this.climate = climate;
    }

    public String getTravelStyle() {
        return travelStyle;
    }

    public void setTravelStyle(String travelStyle) {
        this.travelStyle = travelStyle;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }

    public double getAvgCostPerDay() {
        return avgCostPerDay;
    }

    public void setAvgCostPerDay(double avgCostPerDay) {
        this.avgCostPerDay = avgCostPerDay;
    }

    public int getBestSeasonStart() {
        return bestSeasonStart;
    }

    public void setBestSeasonStart(int bestSeasonStart) {
        this.bestSeasonStart = bestSeasonStart;
    }

    public int getBestSeasonEnd() {
        return bestSeasonEnd;
    }

    public void setBestSeasonEnd(int bestSeasonEnd) {
        this.bestSeasonEnd = bestSeasonEnd;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    /**
     * Feature vector for clustering: [rating_norm, cost_norm, lat_norm, lon_norm]
     */
    public double[] toFeatureVector() {
        return new double[] {
                avgRating / 5.0,
                Math.min(avgCostPerDay / 500.0, 1.0),
                (latitude + 90) / 180.0,
                (longitude + 180) / 360.0
        };
    }

    /**
     * Check if destination is in its best season for a given travel month.
     */
    public boolean isInBestSeason(int month) {
        if (bestSeasonStart <= bestSeasonEnd) {
            return month >= bestSeasonStart && month <= bestSeasonEnd;
        } else {
            // Wraps year-end e.g. Nov(11) to Feb(2)
            return month >= bestSeasonStart || month <= bestSeasonEnd;
        }
    }

    @Override
    public String toString() {
        return String.format("Destination{id=%d, name='%s', country='%s', rating=%.1f, cost=%.0f/day, climate='%s'}",
                id, name, country, avgRating, avgCostPerDay, climate);
    }
}
