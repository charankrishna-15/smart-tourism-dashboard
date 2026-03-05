package com.tourism.db;

import com.tourism.model.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Destination CRUD operations.
 */
public class DestinationDAO {
    private static final Logger logger = LoggerFactory.getLogger(DestinationDAO.class);
    private final Connection conn;

    public DestinationDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public void insert(Destination d) throws SQLException {
        String sql = """
            INSERT INTO destinations
              (name, country, climate, travel_style, avg_rating, total_reviews,
               avg_cost_per_day, best_season_start, best_season_end,
               latitude, longitude, description, activities)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getCountry());
            ps.setString(3, d.getClimate());
            ps.setString(4, d.getTravelStyle());
            ps.setDouble(5, d.getAvgRating());
            ps.setInt(6, d.getTotalReviews());
            ps.setDouble(7, d.getAvgCostPerDay());
            ps.setInt(8, d.getBestSeasonStart());
            ps.setInt(9, d.getBestSeasonEnd());
            ps.setDouble(10, d.getLatitude());
            ps.setDouble(11, d.getLongitude());
            ps.setString(12, d.getDescription());
            ps.setString(13, d.getActivities());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) d.setId(rs.getInt(1));
            }
        }
        logger.debug("Inserted destination: {}", d.getName());
    }

    public List<Destination> findAll() throws SQLException {
        List<Destination> list = new ArrayList<>();
        String sql = "SELECT * FROM destinations ORDER BY avg_rating DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Destination findById(int id) throws SQLException {
        String sql = "SELECT * FROM destinations WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<Destination> findByClimate(String climate) throws SQLException {
        List<Destination> list = new ArrayList<>();
        String sql = "SELECT * FROM destinations WHERE LOWER(climate) = LOWER(?) ORDER BY avg_rating DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, climate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public List<Destination> findByStyle(String style) throws SQLException {
        List<Destination> list = new ArrayList<>();
        String sql = "SELECT * FROM destinations WHERE LOWER(travel_style) = LOWER(?) ORDER BY avg_rating DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, style);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public List<Destination> findByMaxCostPerDay(double maxCost) throws SQLException {
        List<Destination> list = new ArrayList<>();
        String sql = "SELECT * FROM destinations WHERE avg_cost_per_day <= ? ORDER BY avg_rating DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, maxCost);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public void updateRating(int destinationId, double newRating, int newTotalReviews) throws SQLException {
        String sql = "UPDATE destinations SET avg_rating=?, total_reviews=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newRating);
            ps.setInt(2, newTotalReviews);
            ps.setInt(3, destinationId);
            ps.executeUpdate();
        }
    }

    private Destination map(ResultSet rs) throws SQLException {
        return new Destination(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("country"),
            rs.getString("climate"),
            rs.getString("travel_style"),
            rs.getDouble("avg_rating"),
            rs.getInt("total_reviews"),
            rs.getDouble("avg_cost_per_day"),
            rs.getInt("best_season_start"),
            rs.getInt("best_season_end"),
            rs.getDouble("latitude"),
            rs.getDouble("longitude"),
            rs.getString("description"),
            rs.getString("activities")
        );
    }
}
