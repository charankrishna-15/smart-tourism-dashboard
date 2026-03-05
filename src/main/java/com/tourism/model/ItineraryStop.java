package com.tourism.model;

/**
 * One stop (destination) within an Itinerary.
 */
public class ItineraryStop {
    private int order;
    private Destination destination;
    private int daysSpent;
    private double costForStop;
    private String notes;

    public ItineraryStop(int order, Destination destination, int daysSpent) {
        this.order = order;
        this.destination = destination;
        this.daysSpent = daysSpent;
        this.costForStop = destination.getAvgCostPerDay() * daysSpent;
    }

    public int getOrder() {
        return order;
    }

    public Destination getDestination() {
        return destination;
    }

    public int getDaysSpent() {
        return daysSpent;
    }

    public double getCostForStop() {
        return costForStop;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return String.format("  Stop %d: %s (%s) — %d days, ₹%.0f",
                order, destination.getName(), destination.getCountry(), daysSpent, costForStop);
    }
}
