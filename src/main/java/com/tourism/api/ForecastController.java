package com.tourism.api;

import com.tourism.service.ForecastService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Forecast Engine (Module 3)
 * GET /api/forecast?destId=1&destName=Taj+Mahal&month=3
 */
@RestController
@RequestMapping("/api/forecast")
@CrossOrigin(origins = "*")
public class ForecastController {

    private final ForecastService forecastService = new ForecastService();

    @GetMapping
    public Map<String, Object> getForecast(
            @RequestParam int destId,
            @RequestParam String destName,
            @RequestParam(defaultValue = "3") int month) {

        try {
            String summary = forecastService.getTrendSummary(destId, destName);
            List<Integer> peakMonths = forecastService.getPeakMonths(destId);
            double forecast = forecastService.forecastVisitors(destId, month);

            return Map.of(
                    "destId", destId,
                    "destName", destName,
                    "summary", summary,
                    "peakMonths", peakMonths,
                    "forecastedVisitors", forecast,
                    "month", month);
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }
}
