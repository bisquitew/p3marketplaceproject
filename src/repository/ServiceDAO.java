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

    public Service findById(long id) throws SQLException {
        String sql = "SELECT * FROM services WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Service> findAllActive() throws SQLException {
        String sql = "SELECT * FROM services WHERE active = TRUE";
        List<Service> services = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) services.add(mapRow(rs));
        }
        return services;
    }

    public List<Service> search(String category, String city) throws SQLException {
        String sql = "SELECT * FROM services WHERE active = 1 AND " +
                "(category LIKE ? OR ? = '') AND (city LIKE ? OR ? = '')";
        List<Service> list = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + category + "%");
            stmt.setString(2, category);
            stmt.setString(3, "%" + city + "%");
            stmt.setString(4, city);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Service> findByHandyman(long handymanId) throws SQLException {
        String sql = "SELECT * FROM services WHERE handyman_id = ?";
        List<Service> list = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, handymanId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean toggleActiveForHandyman(long id, long handymanId) throws SQLException {
        String sql = "UPDATE services SET active = NOT active WHERE id = ? AND handyman_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.setLong(2, handymanId);
            return stmt.executeUpdate() > 0;
        }
    }

    private Service mapRow(ResultSet rs) throws SQLException {
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
        return s;
    }
}