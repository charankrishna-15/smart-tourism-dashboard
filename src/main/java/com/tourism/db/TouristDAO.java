package com.tourism.db;

import com.tourism.model.Tourist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Tourist CRUD operations.
 */
public class TouristDAO {
    private static final Logger logger = LoggerFactory.getLogger(TouristDAO.class);
    private final Connection conn;

    public TouristDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public void insert(Tourist t) throws SQLException {
        String sql = """
            INSERT INTO tourists (name, email, preferred_climate, travel_style, budget, max_travel_days)
            VALUES (?,?,?,?,?,?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getName());
            ps.setString(2, t.getEmail());
            ps.setString(3, t.getPreferredClimate());
            ps.setString(4, t.getTravelStyle());
            ps.setDouble(5, t.getBudget());
            ps.setInt(6, t.getMaxTravelDays());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) t.setId(rs.getInt(1));
            }
        }
        logger.debug("Inserted tourist: {}", t.getName());
    }

    public Tourist findById(int id) throws SQLException {
        String sql = "SELECT * FROM tourists WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<Tourist> findAll() throws SQLException {
        List<Tourist> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tourists ORDER BY name")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Tourist> findByStyle(String style) throws SQLException {
        List<Tourist> list = new ArrayList<>();
        String sql = "SELECT * FROM tourists WHERE LOWER(travel_style) = LOWER(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, style);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    private Tourist map(ResultSet rs) throws SQLException {
        return new Tourist(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("preferred_climate"),
            rs.getString("travel_style"),
            rs.getDouble("budget"),
            rs.getInt("max_travel_days")
        );
    }
}
