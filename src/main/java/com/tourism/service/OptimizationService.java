package com.tourism.service;

import com.tourism.db.ItineraryDAO;
import com.tourism.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Optimization Unit — Travel time and cost minimization.
 *
 * Implements:
 * • Nearest-neighbor heuristic for Travelling Salesman Problem (TSP) —
 * minimizes travel distance.
 * • Budget-constrained greedy destination selection.
 * • Days-allocation optimization (more days at high-rated stops).
 * • Optimization score: composite measure of budget efficiency & routing
 * quality.
 */
public class OptimizationService {
    private static final Logger logger = LoggerFactory.getLogger(OptimizationService.class);
    private final ItineraryDAO itineraryDAO;

    // Earth radius in km
    private static final double R = 6371.0;

    public OptimizationService() {
        this.itineraryDAO = new ItineraryDAO();
    }

    /**
     * Build an optimized itinerary from a list of recommended destinations.
     *
     * @param tourist    the traveller
     * @param candidates shortlisted destinations (from recommendation engine)
     * @param startDate  trip start date
     * @param saveToDb   whether to persist the result
     */
    public Itinerary buildOptimizedItinerary(Tourist tourist, List<Destination> candidates,
            LocalDate startDate, boolean saveToDb) throws SQLException {
        logger.info("Building optimized itinerary for {} with {} candidates", tourist.getName(), candidates.size());

        // Step 1: Budget-constrained greedy selection
        List<Destination> selected = greedyBudgetSelection(candidates, tourist.getBudget(), tourist.getMaxTravelDays());
        if (selected.isEmpty()) {
            logger.warn("No destinations fit within budget.");
            return new Itinerary();
        }

        // Step 2: TSP nearest-neighbor route ordering to minimize travel
        List<Destination> ordered = nearestNeighborTSP(selected);

        // Step 3: Days allocation — proportional to rating
        Map<Integer, Integer> daysAllocation = allocateDays(ordered, tourist.getMaxTravelDays(), tourist.getBudget());

        // Step 4: Build Itinerary
        Itinerary itinerary = new Itinerary(0, tourist.getId(), tourist.getName(), startDate);
        for (int i = 0; i < ordered.size(); i++) {
            Destination d = ordered.get(i);
            int days = daysAllocation.getOrDefault(d.getId(), 1);
            itinerary.addStop(new ItineraryStop(i + 1, d, days));
        }

        // Step 5: Score
        double score = computeOptimizationScore(itinerary, tourist);
        itinerary.setOptimizationScore(score);
        itinerary.setNotes(String.format("Optimized via TSP heuristic + budget greedy. Score: %.1f/100", score));

        if (saveToDb) {
            itineraryDAO.save(itinerary);
        }

        logger.info("Itinerary built: {} stops, ₹{} total, {}km travel, score={}",
                itinerary.getStops().size(),
                String.format("%.0f", itinerary.getTotalCost()),
                String.format("%.0f", itinerary.getTotalDistanceKm()),
                String.format("%.1f", score));
        return itinerary;
    }

    /**
     * Greedy approach: add destinations by rating-to-cost ratio until budget/days
     * exhausted.
     */
    private List<Destination> greedyBudgetSelection(List<Destination> candidates,
            double budget, int maxDays) {
        // Sort by rating/cost ratio (value for money)
        List<Destination> sorted = candidates.stream()
                .sorted((a, b) -> Double.compare(
                        b.getAvgRating() / Math.max(b.getAvgCostPerDay(), 1),
                        a.getAvgRating() / Math.max(a.getAvgCostPerDay(), 1)))
                .collect(Collectors.toList());

        List<Destination> selected = new ArrayList<>();
        double remainingBudget = budget;
        int remainingDays = maxDays;

        for (Destination d : sorted) {
            int minDays = 2; // at least 2 days per destination
            double minCost = d.getAvgCostPerDay() * minDays;
            if (minDays <= remainingDays && minCost <= remainingBudget) {
                selected.add(d);
                // Reserve budget for min stay
                remainingBudget -= minCost;
                remainingDays -= minDays;
            }
            if (remainingDays < 2 || selected.size() >= 6)
                break; // limit to 6 stops
        }
        logger.debug("Budget selection: {} destinations chosen from {}", selected.size(), candidates.size());
        return selected;
    }

    /**
     * Nearest-neighbor TSP heuristic.
     * Starts from the geographically "westernmost" destination and greedily picks
     * the nearest unvisited.
     */
    private List<Destination> nearestNeighborTSP(List<Destination> dests) {
        if (dests.size() <= 1)
            return dests;

        List<Destination> unvisited = new ArrayList<>(dests);
        List<Destination> route = new ArrayList<>();

        // Start from westernmost (smallest longitude)
        Destination current = unvisited.stream()
                .min(Comparator.comparingDouble(Destination::getLongitude))
                .orElse(unvisited.get(0));
        unvisited.remove(current);
        route.add(current);

        while (!unvisited.isEmpty()) {
            final Destination cur = current;
            Destination nearest = unvisited.stream()
                    .min(Comparator.comparingDouble(d -> haversine(
                            cur.getLatitude(), cur.getLongitude(),
                            d.getLatitude(), d.getLongitude())))
                    .orElseThrow();
            unvisited.remove(nearest);
            route.add(nearest);
            current = nearest;
        }
        logger.debug("TSP route: {}", route.stream().map(Destination::getName).collect(Collectors.joining(" → ")));
        return route;
    }

    /**
     * Allocate remaining days proportionally (by rating) across destinations.
     */
    private Map<Integer, Integer> allocateDays(List<Destination> dests,
            int maxDays, double budget) {
        Map<Integer, Integer> alloc = new HashMap<>();
        if (dests.isEmpty())
            return alloc;

        int basePerStop = 2;
        int used = basePerStop * dests.size();
        int extra = Math.max(0, maxDays - used);

        double totalRating = dests.stream().mapToDouble(Destination::getAvgRating).sum();
        int extraAssigned = 0;

        for (int i = 0; i < dests.size(); i++) {
            Destination d = dests.get(i);
            int extraDays = (i < dests.size() - 1)
                    ? (int) Math.floor(extra * (d.getAvgRating() / Math.max(totalRating, 1.0)))
                    : extra - extraAssigned;
            alloc.put(d.getId(), basePerStop + Math.max(0, extraDays));
            extraAssigned += Math.max(0, extraDays);
        }
        return alloc;
    }

    /**
     * Composite optimization score (0-100):
     * - Budget utilization (40 pts): used 70-95% of budget is ideal
     * - Route efficiency (30 pts): low distance relative to stops
     * - Days coverage (30 pts): used close to available days
     */
    private double computeOptimizationScore(Itinerary it, Tourist tourist) {
        double budgetUsed = it.getTotalCost() / Math.max(tourist.getBudget(), 1);
        double budgetScore = budgetUsed >= 0.70 && budgetUsed <= 0.95
                ? 40
                : (budgetUsed > 0.50 ? 25 : 10);

        double avgDistPerStop = it.getStops().size() > 1
                ? it.getTotalDistanceKm() / (it.getStops().size() - 1)
                : 0;
        double routeScore = avgDistPerStop < 1000 ? 30
                : avgDistPerStop < 3000 ? 20 : 10;

        double daysUsed = (double) it.getTotalDays() / Math.max(tourist.getMaxTravelDays(), 1);
        double daysScore = daysUsed >= 0.80 && daysUsed <= 1.00 ? 30
                : daysUsed > 0.60 ? 20 : 10;

        return Math.min(budgetScore + routeScore + daysScore, 100.0);
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
