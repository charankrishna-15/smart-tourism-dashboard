package com.tourism.model;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

/**
 * Represents an optimized travel itinerary for a tourist.
 */
public class Itinerary {
    private int id;
    private int touristId;
    private String touristName;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<ItineraryStop> stops;
    private double totalCost;
    private double totalDistanceKm;
    private int totalDays;
    private double optimizationScore; // 0-100, higher is better
    private String notes;

    public Itinerary() {
        this.stops = new ArrayList<>();
    }

    public Itinerary(int id, int touristId, String touristName, LocalDate startDate) {
        this.id = id;
        this.touristId = touristId;
        this.touristName = touristName;
        this.startDate = startDate;
        this.stops = new ArrayList<>();
    }

    public void addStop(ItineraryStop stop) {
        stops.add(stop);
        recalculate();
    }

    private void recalculate() {
        totalCost = stops.stream().mapToDouble(s -> s.getCostForStop()).sum();
        totalDays = stops.stream().mapToInt(ItineraryStop::getDaysSpent).sum();
        if (startDate != null) {
            endDate = startDate.plusDays(totalDays);
        }
        // Calculate total distance (sequential haversine between stops)
        totalDistanceKm = 0;
        for (int i = 1; i < stops.size(); i++) {
            totalDistanceKm += haversine(
                    stops.get(i - 1).getDestination().getLatitude(),
                    stops.get(i - 1).getDestination().getLongitude(),
                    stops.get(i).getDestination().getLatitude(),
                    stops.get(i).getDestination().getLongitude());
        }
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTouristId() {
        return touristId;
    }

    public void setTouristId(int touristId) {
        this.touristId = touristId;
    }

    public String getTouristName() {
        return touristName;
    }

    public void setTouristName(String touristName) {
        this.touristName = touristName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<ItineraryStop> getStops() {
        return stops;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public double getOptimizationScore() {
        return optimizationScore;
    }

    public void setOptimizationScore(double optimizationScore) {
        this.optimizationScore = optimizationScore;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return String.format("Itinerary{id=%d, tourist='%s', stops=%d, days=%d, cost=₹%.0f, dist=%.0fkm, score=%.1f}",
                id, touristName, stops.size(), totalDays, totalCost, totalDistanceKm, optimizationScore);
    }
}
