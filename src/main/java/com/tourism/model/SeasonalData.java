package com.tourism.model;

/**
 * Historical monthly visitor count for a destination (for trend forecasting).
 */
public class SeasonalData {
    private int id;
    private int destinationId;
    private String destinationName;
    private int year;
    private int month;
    private int visitorCount;
    private double avgTemperatureCelsius;
    private double avgPrecipitationMm;

    public SeasonalData() {}

    public SeasonalData(int id, int destinationId, String destinationName,
                        int year, int month, int visitorCount,
                        double avgTemperatureCelsius, double avgPrecipitationMm) {
        this.id = id;
        this.destinationId = destinationId;
        this.destinationName = destinationName;
        this.year = year;
        this.month = month;
        this.visitorCount = visitorCount;
        this.avgTemperatureCelsius = avgTemperatureCelsius;
        this.avgPrecipitationMm = avgPrecipitationMm;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDestinationId() { return destinationId; }
    public void setDestinationId(int destinationId) { this.destinationId = destinationId; }

    public String getDestinationName() { return destinationName; }
    public void setDestinationName(String destinationName) { this.destinationName = destinationName; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public int getVisitorCount() { return visitorCount; }
    public void setVisitorCount(int visitorCount) { this.visitorCount = visitorCount; }

    public double getAvgTemperatureCelsius() { return avgTemperatureCelsius; }
    public void setAvgTemperatureCelsius(double avgTemperatureCelsius) { this.avgTemperatureCelsius = avgTemperatureCelsius; }

    public double getAvgPrecipitationMm() { return avgPrecipitationMm; }
    public void setAvgPrecipitationMm(double avgPrecipitationMm) { this.avgPrecipitationMm = avgPrecipitationMm; }

    @Override
    public String toString() {
        return String.format("SeasonalData{dest='%s', %d/%02d, visitors=%d, temp=%.1f°C}",
                destinationName, year, month, visitorCount, avgTemperatureCelsius);
    }
}
