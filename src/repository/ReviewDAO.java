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

    public List<Review> findByBookingId(long bookingId) throws SQLException {
        String sql = "SELECT id, booking_id, rating, comment FROM reviews WHERE booking_id = ?";
        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
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
        }
        return reviews;
    }

    public double calculateHandymanAverageRating(long handymanId) throws SQLException {
        String sql = """
                SELECT AVG(r.rating) AS avg_rating
                FROM reviews r
                JOIN bookings b ON r.booking_id = b.id
                JOIN services s ON b.service_id = s.id
                WHERE s.handyman_id = ?
                """;
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, handymanId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble("avg_rating");
                    if (rs.wasNull()) return 0.0;
                    return Math.round(avg * 100.0) / 100.0;
                }
            }
        }
        return 0.0;
    }

    /**
     * Returns an int[5]:
     * index 0 -> count of 1-star
     * index 1 -> count of 2-star
     * ...
     * index 4 -> count of 5-star
     */
    public int[] getRatingBreakdownForHandyman(long handymanId) throws SQLException {
        String sql = """
                SELECT r.rating, COUNT(*) AS cnt
                FROM reviews r
                JOIN bookings b ON r.booking_id = b.id
                JOIN services s ON b.service_id = s.id
                WHERE s.handyman_id = ?
                GROUP BY r.rating
                """;

        int[] breakdown = new int[5];

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, handymanId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int rating = rs.getInt("rating");
                    int cnt = rs.getInt("cnt");
                    if (rating >= 1 && rating <= 5) {
                        breakdown[rating - 1] = cnt;
                    }
                }
            }
        }
        return breakdown;
    }
}
