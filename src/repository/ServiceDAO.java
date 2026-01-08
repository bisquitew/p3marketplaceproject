package repository;

import model.Service;
import util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    public void insert(Service service) throws SQLException {
        String sql = "INSERT INTO services (id, handyman_id, title, description, price, category, city, active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE handyman_id = VALUES(handyman_id), title = VALUES(title), " +
                "description = VALUES(description), price = VALUES(price), category = VALUES(category), " +
                "city = VALUES(city), active = VALUES(active)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, service.getId());
            stmt.setLong(2, service.getHandymanId());
            stmt.setString(3, service.getTitle());
            stmt.setString(4, service.getDescription());
            stmt.setDouble(5, service.getPrice());
            stmt.setString(6, service.getCategory());
            stmt.setString(7, service.getCity());
            stmt.setBoolean(8, service.isActive());
            stmt.executeUpdate();
        }
    }

    public List<Service> findAll() throws SQLException {
        String sql = "SELECT id, handyman_id, title, description, price, category, city, active FROM services";
        List<Service> services = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Service s = new Service(
                        rs.getLong("id"),
                        rs.getLong("handyman_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("category"),
                        rs.getString("city")
                );
                s.setActive(rs.getBoolean("active"));
                services.add(s);
            }
        }
        return services;
    }
}
