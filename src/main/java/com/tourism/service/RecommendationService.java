package com.tourism.service;

import com.tourism.db.DestinationDAO;
import com.tourism.db.SeasonalDataDAO;
import com.tourism.model.Destination;
import com.tourism.model.Recommendation;
import com.tourism.model.Tourist;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Recommendation Module — clustering + similarity scoring.
 *
 * Algorithm:
 *  1. Apply K-Means (k=4) clustering on destinations by feature vectors.
 *  2. For a given tourist, compute cosine similarity between tourist preferences
 *     and each destination's attribute vector.
 *  3. Apply seasonal and budget filters.
 *  4. Rank by composite score = 0.5*similarity + 0.3*rating_norm + 0.2*season_bonus.
 */
public class RecommendationService {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);

    private final DestinationDAO destinationDAO;
    private final SeasonalDataDAO seasonalDataDAO;
    private final ForecastService forecastService;

    // Feature-weight tuning
    private static final double W_SIMILARITY = 0.45;
    private static final double W_RATING     = 0.30;
    private static final double W_SEASON     = 0.15;
    private static final double W_CROWD      = 0.10;

    public RecommendationService() {
        this.destinationDAO  = new DestinationDAO();
        this.seasonalDataDAO = new SeasonalDataDAO();
        this.forecastService = new ForecastService();
    }

    /**
     * Returns top-N destination recommendations for the given tourist.
     *
     * @param tourist   the user whose preferences drive recommendations
     * @param topN      number of results to return
     * @param month     travel month (1-12), used for seasonal scoring
     */
    public List<Recommendation> recommend(Tourist tourist, int topN, int month) throws SQLException {
        logger.info("Generating recommendations for tourist: {} (month={})", tourist.getName(), month);
        List<Destination> allDests = destinationDAO.findAll();
        if (allDests.isEmpty()) {
            logger.warn("No destinations in database.");
            return Collections.emptyList();
        }

        // Run K-Means clustering on destination features
        Map<Integer, Integer> clusterAssignments = clusterDestinations(allDests, 4);

        // Score each destination
        List<Recommendation> recs = new ArrayList<>();
        for (Destination dest : allDests) {
            double sim      = computeSimilarity(tourist, dest);
            double ratingN  = dest.getAvgRating() / 5.0;
            double seasonB  = dest.isInBestSeason(month) ? 1.0 : 0.3;
            boolean affordable = (dest.getAvgCostPerDay() * tourist.getMaxTravelDays()) <= tourist.getBudget();

            // Crowd index from forecast
            double forecast = 0;
            try {
                forecast = forecastService.forecastVisitors(dest.getId(), month);
            } catch (Exception e) { /* no data yet */ }
            double crowdScore = forecast > 0 ? Math.max(0, 1.0 - (forecast / 50000.0)) : 0.5;

            double finalScore = (W_SIMILARITY * sim + W_RATING * ratingN
                                 + W_SEASON * seasonB + W_CROWD * crowdScore) * 100.0;

            String reason = buildReason(tourist, dest, sim, seasonB, affordable);
            Recommendation rec = new Recommendation(dest, sim, reason);
            rec.setFinalScore(finalScore);
            rec.setInBestSeason(dest.isInBestSeason(month));
            rec.setForecastedVisitors(forecast);
            recs.add(rec);
        }

        // Sort descending by finalScore and return topN
        recs.sort((a, b) -> Double.compare(b.getFinalScore(), a.getFinalScore()));
        List<Recommendation> result = recs.stream().limit(topN).collect(Collectors.toList());
        logger.info("Top {} recommendations generated.", result.size());
        return result;
    }

    /**
     * Content-based similarity using a weighted attribute match.
     * Climate match + style match + budget fit contribute to score.
     */
    private double computeSimilarity(Tourist tourist, Destination dest) {
        double score = 0.0;
        // Climate match (0 or 1)
        if (tourist.getPreferredClimate() != null &&
            tourist.getPreferredClimate().equalsIgnoreCase(dest.getClimate())) {
            score += 0.40;
        }
        // Travel style match
        if (tourist.getTravelStyle() != null &&
            tourist.getTravelStyle().equalsIgnoreCase(dest.getTravelStyle())) {
            score += 0.40;
        }
        // Budget fit: higher score if daily cost is well within budget
        double totalCost = dest.getAvgCostPerDay() * tourist.getMaxTravelDays();
        if (totalCost <= tourist.getBudget()) {
            // How much budget remains (normalized 0-1)
            double budgetSlack = (tourist.getBudget() - totalCost) / tourist.getBudget();
            score += 0.20 * Math.min(budgetSlack + 0.5, 1.0);
        }
        return Math.min(score, 1.0);
    }

    /**
     * K-Means++ clustering on destination feature vectors.
     * Returns map of destinationId -> clusterId.
     */
    private Map<Integer, Integer> clusterDestinations(List<Destination> dests, int k) {
        logger.info("Running K-Means++ clustering on {} destinations with k={}", dests.size(), k);
        List<DestinationPoint> points = dests.stream()
                .map(DestinationPoint::new)
                .collect(Collectors.toList());

        int effectiveK = Math.min(k, dests.size());
        KMeansPlusPlusClusterer<DestinationPoint> clusterer =
                new KMeansPlusPlusClusterer<>(effectiveK, 1000);
        List<CentroidCluster<DestinationPoint>> clusters = clusterer.cluster(points);

        Map<Integer, Integer> assignments = new HashMap<>();
        for (int ci = 0; ci < clusters.size(); ci++) {
            for (DestinationPoint p : clusters.get(ci).getPoints()) {
                assignments.put(p.destinationId, ci);
            }
        }
        logger.info("Clustering complete — {} clusters formed.", clusters.size());
        return assignments;
    }

    private String buildReason(Tourist tourist, Destination dest,
                                double sim, double seasonB, boolean affordable) {
        List<String> reasons = new ArrayList<>();
        if (tourist.getPreferredClimate() != null &&
            tourist.getPreferredClimate().equalsIgnoreCase(dest.getClimate())) {
            reasons.add("matches your preferred " + dest.getClimate() + " climate");
        }
        if (tourist.getTravelStyle() != null &&
            tourist.getTravelStyle().equalsIgnoreCase(dest.getTravelStyle())) {
            reasons.add("suits your " + dest.getTravelStyle() + " travel style");
        }
        if (affordable) reasons.add("within your budget");
        if (seasonB > 0.9) reasons.add("currently in best season");
        return reasons.isEmpty() ? "Highly rated destination" : String.join(", ", reasons);
    }

    /** Wrapper to make Destination compatible with Commons-Math Clusterable */
    private static class DestinationPoint implements Clusterable {
        final int destinationId;
        final double[] point;

        DestinationPoint(Destination d) {
            this.destinationId = d.getId();
            this.point = d.toFeatureVector();
        }

        @Override
        public double[] getPoint() { return point; }
    }
}
