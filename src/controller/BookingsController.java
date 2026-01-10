package repository;

import model.Service;
import util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    public void insert(Service s) throws SQLException {
        String sql = "INSERT INTO services (id, handyman_id, title, description, price, category, city, active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, s.getId());
            stmt.setLong(2, s.getHandymanId());
            stmt.setString(3, s.getTitle());
            stmt.setString(4, s.getDescription());
            stmt.setDouble(5, s.getPrice());
            stmt.setString(6, s.getCategory());
            stmt.setString(7, s.getCity());
            stmt.setBoolean(8, s.isActive());

            stmt.executeUpdate();
        }
    }

    public List<Service> findAllActive() throws SQLException {
        String sql = "SELECT * FROM services WHERE active = 1";
        List<Service> list = new ArrayList<>();

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));
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

    public boolean toggleActiveForHandyman(long id, long handymanId) throws SQLException {
        String sql = "UPDATE services SET active = NOT active WHERE id = ? AND handyman_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.setLong(2, handymanId);
            return stmt.executeUpdate() > 0;
        }
    }

    // ✅ NEW METHOD — REQUIRED BY BookingsController
    public Service findById(long id) throws SQLException {
        String sql = "SELECT * FROM services WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    private Service mapRow(ResultSet rs) throws SQLException {
        return new Service(
                rs.getLong("id"),
                rs.getLong("handyman_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getDouble("price"),
                rs.getString("category"),
                rs.getString("city")
        );
    }
}
