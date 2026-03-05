package com.tourism.report;

import com.tourism.model.*;
import com.tourism.service.ForecastService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Reporting Module — generates visual charts for itineraries and seasonal
 * trends.
 *
 * Produces:
 * 1. Itinerary cost breakdown bar chart
 * 2. Itinerary days allocation pie chart
 * 3. Seasonal visitor trend line chart (forecast vs actual)
 * 4. Destination ratings bar chart
 * 5. Recommendation score comparison chart
 */
public class ReportGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ReportGenerator.class);
    private static final String OUTPUT_DIR = "reports";

    private static final String[] MONTH_NAMES = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
            "Nov", "Dec" };

    private static final Color DARK_BG = new Color(30, 30, 46);
    private static final Color ACCENT1 = new Color(108, 92, 231);
    private static final Color ACCENT2 = new Color(253, 121, 168);
    private static final Color ACCENT3 = new Color(85, 239, 196);
    private static final Color ACCENT4 = new Color(253, 203, 110);
    private static final Color ACCENT5 = new Color(116, 185, 255);
    private static final Color TEXT_COLOR = new Color(220, 220, 230);

    private final ForecastService forecastService;

    public ReportGenerator() {
        this.forecastService = new ForecastService();
        new File(OUTPUT_DIR).mkdirs();
    }

    // ── 1. Itinerary Cost Breakdown ───────────────────────────────────────────

    public String generateItineraryCostChart(Itinerary itinerary) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (ItineraryStop stop : itinerary.getStops()) {
            dataset.addValue(stop.getCostForStop(), "Cost (INR)", stop.getDestination().getName());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Itinerary Cost Breakdown — " + itinerary.getTouristName(),
                "Destination", "Cost (INR)", dataset,
                PlotOrientation.VERTICAL, false, true, false);

        styleBarChart(chart, dataset,
                new Color[] { ACCENT1, ACCENT2, ACCENT3, ACCENT4, ACCENT5, new Color(255, 118, 117) });

        String path = OUTPUT_DIR + "/itinerary_cost_" + itinerary.getTouristId() + ".png";
        ChartUtils.saveChartAsPNG(new File(path), chart, 800, 450);
        logger.info("Cost chart saved: {}", path);
        return path;
    }

    // ── 2. Days Allocation Pie Chart ─────────────────────────────────────────

    public String generateDaysAllocationChart(Itinerary itinerary) throws IOException {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (ItineraryStop stop : itinerary.getStops()) {
            dataset.setValue(stop.getDestination().getName(), stop.getDaysSpent());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Days Allocation — " + itinerary.getTouristName(), dataset, true, true, false);

        PiePlot<?> plot = (PiePlot<?>) chart.getPlot();
        plot.setBackgroundPaint(DARK_BG);
        plot.setOutlinePaint(null);
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 11));
        plot.setLabelBackgroundPaint(new Color(50, 50, 70));
        plot.setLabelOutlinePaint(null);
        plot.setLabelShadowPaint(null);
        plot.setLabelPaint(TEXT_COLOR);
        chart.setBackgroundPaint(DARK_BG);
        chart.getTitle().setPaint(TEXT_COLOR);
        chart.getLegend().setBackgroundPaint(DARK_BG);
        chart.getLegend().setItemPaint(TEXT_COLOR);

        Color[] pieColors = { ACCENT1, ACCENT2, ACCENT3, ACCENT4, ACCENT5, new Color(255, 118, 117) };
        int i = 0;
        for (Object key : dataset.getKeys()) {
            plot.setSectionPaint((Comparable<?>) key, pieColors[i % pieColors.length]);
            i++;
        }

        String path = OUTPUT_DIR + "/itinerary_days_" + itinerary.getTouristId() + ".png";
        ChartUtils.saveChartAsPNG(new File(path), chart, 600, 450);
        logger.info("Days allocation chart saved: {}", path);
        return path;
    }

    // ── 3. Seasonal Trend Line Chart ──────────────────────────────────────────

    public String generateSeasonalTrendChart(int destinationId, String destinationName,
            List<SeasonalData> history) throws IOException, SQLException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Historical data by month avg
        double[] historicalAvgs = new double[12];
        int[] historicalCounts = new int[12];
        for (SeasonalData sd : history) {
            historicalAvgs[sd.getMonth() - 1] += sd.getVisitorCount();
            historicalCounts[sd.getMonth() - 1] += 1;
        }
        for (int m = 0; m < 12; m++) {
            if (historicalCounts[m] > 0)
                dataset.addValue(historicalAvgs[m] / historicalCounts[m],
                        "Historical Avg", MONTH_NAMES[m]);
        }

        // Forecasted data
        double[] forecast = forecastService.forecastYearlyProfile(destinationId);
        for (int m = 0; m < 12; m++) {
            if (forecast[m] > 0)
                dataset.addValue(forecast[m], "2025 Forecast", MONTH_NAMES[m]);
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Seasonal Visitor Trend — " + destinationName,
                "Month", "Visitors", dataset, PlotOrientation.VERTICAL, true, true, false);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(40, 40, 60));
        plot.setDomainGridlinePaint(new Color(80, 80, 100));
        plot.setRangeGridlinePaint(new Color(80, 80, 100));
        chart.setBackgroundPaint(DARK_BG);
        chart.getTitle().setPaint(TEXT_COLOR);
        chart.getLegend().setBackgroundPaint(DARK_BG);
        chart.getLegend().setItemPaint(TEXT_COLOR);
        plot.getRangeAxis().setTickLabelPaint(TEXT_COLOR);
        plot.getRangeAxis().setLabelPaint(TEXT_COLOR);
        plot.getDomainAxis().setTickLabelPaint(TEXT_COLOR);
        plot.getDomainAxis().setLabelPaint(TEXT_COLOR);
        ((CategoryAxis) plot.getDomainAxis())
                .setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, ACCENT3);
        renderer.setSeriesPaint(1, ACCENT2);
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        renderer.setSeriesStroke(1, new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[] { 8.0f, 4.0f }, 0.0f));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesVisible(1, true);
        plot.setRenderer(renderer);

        String path = OUTPUT_DIR + "/seasonal_trend_" + destinationId + ".png";
        ChartUtils.saveChartAsPNG(new File(path), chart, 900, 450);
        logger.info("Seasonal trend chart saved: {}", path);
        return path;
    }

    // ── 4. Destination Ratings Bar Chart ─────────────────────────────────────

    public String generateRatingsChart(List<Destination> destinations) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        destinations.stream()
                .sorted((a, b) -> Double.compare(b.getAvgRating(), a.getAvgRating()))
                .limit(10)
                .forEach(d -> dataset.addValue(d.getAvgRating(), "Rating", d.getName()));

        JFreeChart chart = ChartFactory.createBarChart(
                "Top Destination Ratings", "Destination", "Rating (out of 5)",
                dataset, PlotOrientation.HORIZONTAL, false, true, false);

        styleBarChart(chart, dataset, new Color[] { ACCENT5 });
        CategoryPlot plot = chart.getCategoryPlot();
        plot.getRangeAxis().setRange(0, 5.5);

        String path = OUTPUT_DIR + "/destination_ratings.png";
        ChartUtils.saveChartAsPNG(new File(path), chart, 800, 500);
        logger.info("Ratings chart saved: {}", path);
        return path;
    }

    // ── 5. Recommendation Score Comparison ───────────────────────────────────

    public String generateRecommendationChart(List<Recommendation> recommendations,
            String touristName) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Recommendation rec : recommendations) {
            dataset.addValue(rec.getFinalScore(), "Match Score",
                    rec.getDestination().getName());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Recommendation Scores — " + touristName,
                "Destination", "Match Score (0-100)",
                dataset, PlotOrientation.VERTICAL, false, true, false);

        styleBarChart(chart, dataset, new Color[] { ACCENT2 });

        String path = OUTPUT_DIR + "/recommendations_" + touristName.replace(" ", "_") + ".png";
        ChartUtils.saveChartAsPNG(new File(path), chart, 900, 450);
        logger.info("Recommendation chart saved: {}", path);
        return path;
    }

    // ── Shared chart styling helper ───────────────────────────────────────────

    private void styleBarChart(JFreeChart chart, DefaultCategoryDataset dataset, Color[] colors) {
        chart.setBackgroundPaint(DARK_BG);
        chart.getTitle().setPaint(TEXT_COLOR);
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 16));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(40, 40, 60));
        plot.setOutlinePaint(null);
        plot.setDomainGridlinePaint(new Color(70, 70, 90));
        plot.setRangeGridlinePaint(new Color(70, 70, 90));
        plot.getRangeAxis().setTickLabelPaint(TEXT_COLOR);
        plot.getRangeAxis().setLabelPaint(TEXT_COLOR);
        plot.getDomainAxis().setTickLabelPaint(TEXT_COLOR);
        plot.getDomainAxis().setLabelPaint(TEXT_COLOR);
        ((CategoryAxis) plot.getDomainAxis())
                .setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setMaximumBarWidth(0.12);
        renderer.setItemMargin(0.05);

        int colCount = dataset.getColumnCount();
        for (int c = 0; c < colCount; c++) {
            renderer.setSeriesItemLabelPaint(0, TEXT_COLOR);
            Color barColor = colors[c % colors.length];
            renderer.setSeriesPaint(0, barColor);
        }
        // Single-series: paint all bars with gradient variety
        if (colors.length == 1) {
            Color base = colors[0];
            for (int c = 0; c < colCount; c++) {
                float ratio = (float) c / Math.max(colCount - 1, 1);
                Color blended = blendColor(base, ACCENT3, ratio);
                renderer.setSeriesPaint(0, base); // reset; JFreeChart single-series
            }
        }
    }

    private Color blendColor(Color a, Color b, float ratio) {
        int r = (int) (a.getRed() * (1 - ratio) + b.getRed() * ratio);
        int g = (int) (a.getGreen() * (1 - ratio) + b.getGreen() * ratio);
        int bl = (int) (a.getBlue() * (1 - ratio) + b.getBlue() * ratio);
        return new Color(r, g, bl);
    }

    // ── Text report ───────────────────────────────────────────────────────────

    public void printItineraryReport(Itinerary it) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.printf("║       ITINERARY REPORT — %-35s║%n", it.getTouristName());
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║  Start Date : %-47s║%n", it.getStartDate());
        System.out.printf("║  End Date   : %-47s║%n", it.getEndDate());
        System.out.printf("║  Total Days : %-47d║%n", it.getTotalDays());
        System.out.printf("║  Total Cost : ₹%-46.0f║%n", it.getTotalCost());
        System.out.printf("║  Distance   : %-44.0f km║%n", it.getTotalDistanceKm());
        System.out.printf("║  Opt. Score : %-44.1f/100║%n", it.getOptimizationScore());
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.println("║  STOPS:                                                      ║");
        for (ItineraryStop stop : it.getStops()) {
            System.out.printf("║  %d. %-20s (%-15s) %2d days ₹%-7.0f║%n",
                    stop.getOrder(),
                    truncate(stop.getDestination().getName(), 20),
                    truncate(stop.getDestination().getCountry(), 15),
                    stop.getDaysSpent(),
                    stop.getCostForStop());
        }
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║  Notes: %-53s║%n", truncate(it.getNotes(), 53));
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private String truncate(String s, int max) {
        if (s == null)
            return "";
        return s.length() <= max ? s : s.substring(0, max - 2) + "..";
    }
}
