package com.tourism.api;

import com.tourism.db.DestinationDAO;
import com.tourism.model.Destination;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

/**
 * REST Controller for Destination Database (Module 1)
 */
@RestController
@RequestMapping("/api/destinations")
@CrossOrigin(origins = "*")
public class DestinationController {

    private final DestinationDAO destinationDAO = new DestinationDAO();

    @GetMapping
    public List<Destination> getAllDestinations() throws SQLException {
        return destinationDAO.findAll();
    }

    @GetMapping("/{id}")
    public Destination getDestination(@PathVariable int id) throws SQLException {
        return destinationDAO.findById(id);
    }
}
