package com.tourism.service;

import com.tourism.db.SeasonalDataDAO;
import com.tourism.model.SeasonalData;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

/**
 * Forecast Engine — Seasonal Trend Modeling.
 *
 * Uses:
 *  • Simple Linear Regression (Apache Commons Math) — extrapolate year-over-year trend.
 *  • Moving average smoothing (3-month window) — for monthly seasonality index.
 *  • Combined model: predicted = trend * seasonal_index.
 */
public class ForecastService {
    private static final Logger logger = LoggerFactory.getLogger(ForecastService.class);
    private final SeasonalDataDAO seasonalDataDAO;

    private static final String[] MONTH_NAMES =
        {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    public ForecastService() {
        this.seasonalDataDAO = new SeasonalDataDAO();
    }

    /**
     * Predict visitor count for a given destination and future month.
     *
     * @param destinationId target destination
     * @param targetMonth   1-12
     * @return forecasted visitor count (0 if insufficient data)
     */
    public double forecastVisitors(int destinationId, int targetMonth) throws SQLException {
        List<SeasonalData> history = seasonalDataDAO.findByDestination(destinationId);
        if (history.size() < 3) {
            logger.warn("Insufficient data to forecast for destination {}", destinationId);
            return 0;
        }

        // Build OLS regression: x = sequential month index, y = visitor_count
        SimpleRegression regression = new SimpleRegression();
        for (int i = 0; i < history.size(); i++) {
            regression.addData(i, history.get(i).getVisitorCount());
        }

        // Trend: expected count at next data point for targetMonth
        double trendValue = regression.predict(history.size());

        // Seasonal index: ratio of avg(targetMonth) / overall avg
        double[] monthlyAvgs = seasonalDataDAO.getMonthlyAverages(destinationId);
        double overallAvg = Arrays.stream(monthlyAvgs).filter(v -> v > 0).average().orElse(1.0);
        double monthAvg = monthlyAvgs[targetMonth - 1];
        double seasonalIndex = (overallAvg > 0 && monthAvg > 0) ? monthAvg / overallAvg : 1.0;

        double predicted = Math.max(0, trendValue * seasonalIndex);
        logger.debug("Forecast for dest={} month={}: trend={}, seasonalIdx={}, predicted={}",
            destinationId, targetMonth,
            String.format("%.0f", trendValue),
            String.format("%.2f", seasonalIndex),
            String.format("%.0f", predicted));
        return predicted;
    }

    /**
     * Predict monthly visitor counts for an entire year.
     *
     * @param destinationId the destination to forecast
     * @return array[12] of predicted visitor counts (index 0 = Jan)
     */
    public double[] forecastYearlyProfile(int destinationId) throws SQLException {
        double[] profile = new double[12];
        for (int m = 1; m <= 12; m++) {
            profile[m - 1] = forecastVisitors(destinationId, m);
        }
        return profile;
    }

    /**
     * Returns the peak season (months with forecasted visitors > 80% of max).
     */
    public List<Integer> getPeakMonths(int destinationId) throws SQLException {
        double[] profile = forecastYearlyProfile(destinationId);
        double max = Arrays.stream(profile).max().orElse(0);
        if (max == 0) return Collections.emptyList();

        List<Integer> peaks = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            if (profile[i] >= 0.8 * max) peaks.add(i + 1);
        }
        return peaks;
    }

    /**
     * Returns a text summary of the seasonal trend for a destination.
     */
    public String getTrendSummary(int destinationId, String destinationName) throws SQLException {
        List<SeasonalData> history = seasonalDataDAO.findByDestination(destinationId);
        if (history.isEmpty()) return destinationName + ": No data available.";

        SimpleRegression reg = new SimpleRegression();
        for (int i = 0; i < history.size(); i++) {
            reg.addData(i, history.get(i).getVisitorCount());
        }

        double slope = reg.getSlope();
        String trend = slope > 100 ? "📈 Growing" : slope < -100 ? "📉 Declining" : "➡ Stable";

        double[] profile = forecastYearlyProfile(destinationId);
        int peakMonth = 0;
        double peakVal = 0;
        for (int i = 0; i < 12; i++) {
            if (profile[i] > peakVal) { peakVal = profile[i]; peakMonth = i; }
        }

        return String.format("%-20s | Trend: %-12s | Peak Month: %-3s | Forecasted peak: %,.0f visitors",
            destinationName, trend, MONTH_NAMES[peakMonth], peakVal);
    }
}
