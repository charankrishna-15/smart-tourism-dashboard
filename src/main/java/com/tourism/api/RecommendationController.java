package com.tourism.api;

import com.tourism.model.Recommendation;
import com.tourism.model.Tourist;
import com.tourism.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Recommendation Engine (Module 2)
 * POST /api/recommend
 * Body: { name, climate, style, budget, days, month }
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RecommendationController {

    private final RecommendationService recommendationService = new RecommendationService();

    @PostMapping("/recommend")
    public List<RecommendationResponse> recommend(@RequestBody Map<String, Object> body) throws SQLException {
        Tourist tourist = new Tourist();
        tourist.setName((String) body.getOrDefault("name", "Tourist"));
        tourist.setPreferredClimate((String) body.getOrDefault("climate", "tropical"));
        tourist.setTravelStyle((String) body.getOrDefault("style", "relaxation"));
        tourist.setBudget(((Number) body.getOrDefault("budget", 100000)).doubleValue());
        tourist.setMaxTravelDays(((Number) body.getOrDefault("days", 14)).intValue());

        int month = ((Number) body.getOrDefault("month", 3)).intValue();
        int topN = ((Number) body.getOrDefault("topN", 5)).intValue();

        List<Recommendation> recs = recommendationService.recommend(tourist, topN, month);
        return recs.stream().map(r -> new RecommendationResponse(
                r.getDestination().getId(),
                r.getDestination().getName(),
                r.getDestination().getCountry(),
                r.getDestination().getClimate(),
                r.getDestination().getTravelStyle(),
                r.getDestination().getAvgRating(),
                r.getDestination().getAvgCostPerDay(),
                r.getFinalScore(),
                r.getMatchReason(),
                r.isInBestSeason(),
                r.getForecastedVisitors())).toList();
    }

    public record RecommendationResponse(
            int id, String name, String country, String climate, String style,
            double rating, double costPerDay, double score,
            String reason, boolean inBestSeason, double forecastedVisitors) {
    }
}
