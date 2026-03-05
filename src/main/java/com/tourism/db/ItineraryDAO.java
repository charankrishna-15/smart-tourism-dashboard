package com.tourism.db;

import com.tourism.model.Itinerary;
import com.tourism.model.ItineraryStop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for saving and loading Itineraries with their stops.
 */
public class ItineraryDAO {
    private static final Logger logger = LoggerFactory.getLogger(ItineraryDAO.class);
    private final Connection conn;
    private final DestinationDAO destinationDAO;

    public ItineraryDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
        this.destinationDAO = new DestinationDAO();
    }

    public void save(Itinerary it) throws SQLException {
        String sql = """
            INSERT INTO itineraries
              (tourist_id, start_date, end_date, total_cost, total_distance_km,
               total_days, optimization_score, notes)
            VALUES (?,?,?,?,?,?,?,?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, it.getTouristId());
            ps.setDate(2, it.getStartDate() != null ? Date.valueOf(it.getStartDate()) : null);
            ps.setDate(3, it.getEndDate()   != null ? Date.valueOf(it.getEndDate())   : null);
            ps.setDouble(4, it.getTotalCost());
            ps.setDouble(5, it.getTotalDistanceKm());
            ps.setInt(6, it.getTotalDays());
            ps.setDouble(7, it.getOptimizationScore());
            ps.setString(8, it.getNotes());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) it.setId(rs.getInt(1));
            }
        }
        saveStops(it);
        logger.info("Saved itinerary id={} for tourist id={}", it.getId(), it.getTouristId());
    }

    private void saveStops(Itinerary it) throws SQLException {
        String sql = """
            INSERT INTO itinerary_stops (itinerary_id, destination_id, stop_order, days_spent, cost_for_stop)
            VALUES (?,?,?,?,?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (ItineraryStop stop : it.getStops()) {
                ps.setInt(1, it.getId());
                ps.setInt(2, stop.getDestination().getId());
                ps.setInt(3, stop.getOrder());
                ps.setInt(4, stop.getDaysSpent());
                ps.setDouble(5, stop.getCostForStop());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public List<Itinerary> findByTourist(int touristId) throws SQLException {
        List<Itinerary> list = new ArrayList<>();
        String sql = """
            SELECT i.*, t.name AS tourist_name
            FROM itineraries i
            JOIN tourists t ON i.tourist_id = t.id
            WHERE i.tourist_id = ?
            ORDER BY i.created_at DESC
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, touristId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Itinerary it = mapItinerary(rs);
                    loadStops(it);
                    list.add(it);
                }
            }
        }
        return list;
    }

    private void loadStops(Itinerary it) throws SQLException {
        String sql = """
            SELECT * FROM itinerary_stops
            WHERE itinerary_id = ? ORDER BY stop_order
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, it.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var dest = destinationDAO.findById(rs.getInt("destination_id"));
                    if (dest != null) {
                        it.getStops().add(new ItineraryStop(
                            rs.getInt("stop_order"), dest, rs.getInt("days_spent")));
                    }
                }
            }
        }
    }

    private Itinerary mapItinerary(ResultSet rs) throws SQLException {
        Itinerary it = new Itinerary();
        it.setId(rs.getInt("id"));
        it.setTouristId(rs.getInt("tourist_id"));
        it.setTouristName(rs.getString("tourist_name"));
        Date sd = rs.getDate("start_date");
        Date ed = rs.getDate("end_date");
        if (sd != null) it.setStartDate(sd.toLocalDate());
        if (ed != null) it.setEndDate(ed.toLocalDate());
        it.setOptimizationScore(rs.getDouble("optimization_score"));
        it.setNotes(rs.getString("notes"));
        return it;
    }
}
