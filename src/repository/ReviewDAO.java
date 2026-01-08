package repository;

import model.Review;
import util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    public void insert(Review review) throws SQLException {
        String sql = "INSERT INTO reviews (id, booking_id, rating, comment) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE booking_id = VALUES(booking_id), rating = VALUES(rating), comment = VALUES(comment)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, review.getId());
            stmt.setLong(2, review.getBookingId());
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getComment());
            stmt.executeUpdate();
        }
    }

    public List<Review> findAll() throws SQLException {
        String sql = "SELECT id, booking_id, rating, comment FROM reviews";
        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Review r = new Review(
                        rs.getLong("id"),
                        rs.getLong("booking_id"),
                        rs.getInt("rating"),
                        rs.getString("comment")
                );
                reviews.add(r);
            }
        }
        return reviews;
    }
}
