package com.tourism.model;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a tourist/user with their preferences and travel history.
 */
public class Tourist {
    private int id;
    private String name;
    private String email;
    private String preferredClimate; // "tropical", "temperate", "arctic", "arid"
    private String travelStyle; // "adventure", "cultural", "relaxation", "eco"
    private double budget; // in INR
    private int maxTravelDays;
    private List<String> preferredActivities;
    private List<Integer> visitedDestinationIds;

    public Tourist() {
        this.preferredActivities = new ArrayList<>();
        this.visitedDestinationIds = new ArrayList<>();
    }

    public Tourist(int id, String name, String email, String preferredClimate,
            String travelStyle, double budget, int maxTravelDays) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.preferredClimate = preferredClimate;
        this.travelStyle = travelStyle;
        this.budget = budget;
        this.maxTravelDays = maxTravelDays;
        this.preferredActivities = new ArrayList<>();
        this.visitedDestinationIds = new ArrayList<>();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPreferredClimate() {
        return preferredClimate;
    }

    public void setPreferredClimate(String preferredClimate) {
        this.preferredClimate = preferredClimate;
    }

    public String getTravelStyle() {
        return travelStyle;
    }

    public void setTravelStyle(String travelStyle) {
        this.travelStyle = travelStyle;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public int getMaxTravelDays() {
        return maxTravelDays;
    }

    public void setMaxTravelDays(int maxTravelDays) {
        this.maxTravelDays = maxTravelDays;
    }

    public List<String> getPreferredActivities() {
        return preferredActivities;
    }

    public void setPreferredActivities(List<String> preferredActivities) {
        this.preferredActivities = preferredActivities;
    }

    public List<Integer> getVisitedDestinationIds() {
        return visitedDestinationIds;
    }

    public void setVisitedDestinationIds(List<Integer> visitedDestinationIds) {
        this.visitedDestinationIds = visitedDestinationIds;
    }

    /**
     * Returns a feature vector for clustering/similarity [budget_norm,
     * travelDays_norm].
     * Budget normalized 0-1 over 10000 max, days normalized 0-1 over 30 max.
     */
    public double[] toFeatureVector() {
        double budgetNorm = Math.min(budget / 10000.0, 1.0);
        double daysNorm = Math.min(maxTravelDays / 30.0, 1.0);
        return new double[] { budgetNorm, daysNorm };
    }

    @Override
    public String toString() {
        return String.format("Tourist{id=%d, name='%s', style='%s', climate='%s', budget=%.0f, days=%d}",
                id, name, travelStyle, preferredClimate, budget, maxTravelDays);
    }
}
