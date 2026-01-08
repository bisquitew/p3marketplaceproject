package repository;

import model.User;
import model.enums.UserRole;
import util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public void insert(User user) throws SQLException {
        String sql = "INSERT INTO users (id, username, password, full_name, email, role, rating) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE username = VALUES(username), password = VALUES(password), " +
                "full_name = VALUES(full_name), email = VALUES(email), role = VALUES(role), rating = VALUES(rating)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, user.getId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getRole().name());
            stmt.setDouble(7, user.getRating());
            stmt.executeUpdate();
        }
    }

    public User findByUsernameAndPassword(String username, String password) throws SQLException {
        String sql = "SELECT id, username, password, full_name, email, role, rating FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User u = new User(
                            rs.getLong("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            UserRole.valueOf(rs.getString("role"))
                    );
                    u.setRating(rs.getDouble("rating"));
                    return u;
                }
            }
        }
        return null;
    }

    public User findById(long id) throws SQLException {
        String sql = "SELECT id, username, password, full_name, email, role, rating FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User u = new User(
                            rs.getLong("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            UserRole.valueOf(rs.getString("role"))
                    );
                    u.setRating(rs.getDouble("rating"));
                    return u;
                }
            }
        }
        return null;
    }

    public void updateRating(long userId, double rating) throws SQLException {
        String sql = "UPDATE users SET rating = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, rating);
            stmt.setLong(2, userId);
            stmt.executeUpdate();
        }
    }

    public List<User> findAll() throws SQLException {
        String sql = "SELECT id, username, password, full_name, email, role, rating FROM users";
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User u = new User(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        UserRole.valueOf(rs.getString("role"))
                );
                u.setRating(rs.getDouble("rating"));
                users.add(u);
            }
        }
        return users;
    }
}
