package com.tourism.db;

import com.tourism.model.SeasonalData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Seasonal visitor data used in trend forecasting.
 */
public class SeasonalDataDAO {
    private static final Logger logger = LoggerFactory.getLogger(SeasonalDataDAO.class);
    private final Connection conn;

    public SeasonalDataDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public void insert(SeasonalData sd) throws SQLException {
        String sql = """
            INSERT INTO seasonal_data
              (destination_id, visit_year, visit_month, visitor_count, avg_temperature_celsius, avg_precipitation_mm)
            VALUES (?,?,?,?,?,?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, sd.getDestinationId());
            ps.setInt(2, sd.getYear());
            ps.setInt(3, sd.getMonth());
            ps.setInt(4, sd.getVisitorCount());
            ps.setDouble(5, sd.getAvgTemperatureCelsius());
            ps.setDouble(6, sd.getAvgPrecipitationMm());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) sd.setId(rs.getInt(1));
            }
        }
    }

    public List<SeasonalData> findByDestination(int destinationId) throws SQLException {
        List<SeasonalData> list = new ArrayList<>();
        String sql = """
            SELECT sd.*, d.name AS dest_name
            FROM seasonal_data sd
            JOIN destinations d ON sd.destination_id = d.id
            WHERE sd.destination_id = ?
            ORDER BY sd.visit_year, sd.visit_month
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, destinationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public List<SeasonalData> findByDestinationAndMonth(int destinationId, int month) throws SQLException {
        List<SeasonalData> list = new ArrayList<>();
        String sql = """
            SELECT sd.*, d.name AS dest_name
            FROM seasonal_data sd
            JOIN destinations d ON sd.destination_id = d.id
            WHERE sd.destination_id = ? AND sd.visit_month = ?
            ORDER BY sd.visit_year
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, destinationId);
            ps.setInt(2, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public List<SeasonalData> findAll() throws SQLException {
        List<SeasonalData> list = new ArrayList<>();
        String sql = """
            SELECT sd.*, d.name AS dest_name
            FROM seasonal_data sd
            JOIN destinations d ON sd.destination_id = d.id
            ORDER BY d.name, sd.visit_year, sd.visit_month
        """;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /**
     * Returns avg monthly visitor counts for a destination across all years.
     */
    public double[] getMonthlyAverages(int destinationId) throws SQLException {
        double[] avgs = new double[12];
        String sql = """
            SELECT visit_month, AVG(visitor_count) AS avg_visitors
            FROM seasonal_data
            WHERE destination_id = ?
            GROUP BY visit_month
            ORDER BY visit_month
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, destinationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int m = rs.getInt("visit_month");
                    avgs[m - 1] = rs.getDouble("avg_visitors");
                }
            }
        }
        return avgs;
    }

    private SeasonalData map(ResultSet rs) throws SQLException {
        return new SeasonalData(
            rs.getInt("id"),
            rs.getInt("destination_id"),
            rs.getString("dest_name"),
            rs.getInt("visit_year"),
            rs.getInt("visit_month"),
            rs.getInt("visitor_count"),
            rs.getDouble("avg_temperature_celsius"),
            rs.getDouble("avg_precipitation_mm")
        );
    }
}
