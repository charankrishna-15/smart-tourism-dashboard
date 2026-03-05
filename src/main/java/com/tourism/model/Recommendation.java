package com.tourism.model;

/**
 * Holds a recommendation result: destination + similarity/match score.
 */
public class Recommendation {
    private Destination destination;
    private double similarityScore;  // 0.0 - 1.0
    private double finalScore;       // composite score (0-100)
    private String matchReason;
    private boolean inBestSeason;
    private double forecastedVisitors;

    public Recommendation(Destination destination, double similarityScore, String matchReason) {
        this.destination = destination;
        this.similarityScore = similarityScore;
        this.matchReason = matchReason;
    }

    public Destination getDestination() { return destination; }
    public void setDestination(Destination destination) { this.destination = destination; }

    public double getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(double similarityScore) { this.similarityScore = similarityScore; }

    public double getFinalScore() { return finalScore; }
    public void setFinalScore(double finalScore) { this.finalScore = finalScore; }

    public String getMatchReason() { return matchReason; }
    public void setMatchReason(String matchReason) { this.matchReason = matchReason; }

    public boolean isInBestSeason() { return inBestSeason; }
    public void setInBestSeason(boolean inBestSeason) { this.inBestSeason = inBestSeason; }

    public double getForecastedVisitors() { return forecastedVisitors; }
    public void setForecastedVisitors(double forecastedVisitors) { this.forecastedVisitors = forecastedVisitors; }

    @Override
    public String toString() {
        return String.format("Recommendation{dest='%s', score=%.1f, reason='%s', bestSeason=%s}",
                destination.getName(), finalScore, matchReason, inBestSeason);
    }
}
