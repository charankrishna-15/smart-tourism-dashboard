package com.tourism.api;

import com.tourism.db.DestinationDAO;
import com.tourism.model.Destination;
import com.tourism.model.Itinerary;
import com.tourism.model.Tourist;
import com.tourism.service.OptimizationService;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Itinerary Optimizer (Module 4)
 * POST /api/optimize
 * Body: { tourist: {...}, destinationIds: [1,2,3,...], startDate: "2026-04-01"
 * }
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class OptimizationController {

    private final OptimizationService optimizationService = new OptimizationService();
    private final DestinationDAO destinationDAO = new DestinationDAO();

    @PostMapping("/optimize")
    public Map<String, Object> optimize(@RequestBody Map<String, Object> body) throws SQLException {
        // Build tourist from request
        @SuppressWarnings("unchecked")
        Map<String, Object> touristData = (Map<String, Object>) body.get("tourist");
        Tourist tourist = new Tourist();
        tourist.setName((String) touristData.getOrDefault("name", "Tourist"));
        tourist.setPreferredClimate((String) touristData.getOrDefault("climate", "tropical"));
        tourist.setTravelStyle((String) touristData.getOrDefault("style", "relaxation"));
        tourist.setBudget(((Number) touristData.getOrDefault("budget", 100000)).doubleValue());
        tourist.setMaxTravelDays(((Number) touristData.getOrDefault("days", 14)).intValue());

        // Fetch candidate destinations
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) body.get("destinationIds");
        List<Destination> candidates = ids.stream()
                .map(id -> {
                    try {
                        return destinationDAO.findById(id);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(d -> d != null)
                .collect(Collectors.toList());

        String startDateStr = (String) body.getOrDefault("startDate", LocalDate.now().plusDays(30).toString());
        LocalDate startDate = LocalDate.parse(startDateStr);

        Itinerary itinerary = optimizationService.buildOptimizedItinerary(tourist, candidates, startDate, true);

        // Build response
        List<Map<String, Object>> stops = itinerary.getStops().stream().map(s -> {
            Map<String, Object> stop = new java.util.LinkedHashMap<>();
            stop.put("order", s.getOrder());
            stop.put("destinationId", s.getDestination().getId());
            stop.put("destinationName", s.getDestination().getName());
            stop.put("country", s.getDestination().getCountry());
            stop.put("emoji", getEmoji(s.getDestination()));
            stop.put("daysSpent", s.getDaysSpent());
            stop.put("costPerDay", s.getDestination().getAvgCostPerDay());
            stop.put("stopTotal", s.getCostForStop());
            return stop;
        }).collect(Collectors.toList());

        return Map.of(
                "touristName", tourist.getName(),
                "startDate", itinerary.getStartDate() != null ? itinerary.getStartDate().toString() : "",
                "endDate", itinerary.getEndDate() != null ? itinerary.getEndDate().toString() : "",
                "stops", stops,
                "totalCost", itinerary.getTotalCost(),
                "totalDays", itinerary.getTotalDays(),
                "totalDistanceKm", itinerary.getTotalDistanceKm(),
                "optimizationScore", itinerary.getOptimizationScore());
    }

    private String getEmoji(Destination d) {
        return switch (d.getTravelStyle()) {
            case "adventure" -> "⛺";
            case "cultural" -> "🏛️";
            case "relaxation" -> "🌴";
            default -> "📍";
        };
    }
}
