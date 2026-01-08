package repository;

import model.Booking;
import model.enums.BookingStatus;
import util.DatabaseConnector;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public void insert(Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings (id, customer_id, service_id, scheduled_datetime, status, address, total_price) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE customer_id = VALUES(customer_id), service_id = VALUES(service_id), " +
                "scheduled_datetime = VALUES(scheduled_datetime), status = VALUES(status), " +
                "address = VALUES(address), total_price = VALUES(total_price)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, booking.getId());
            stmt.setLong(2, booking.getCustomerId());
            stmt.setLong(3, booking.getServiceId());
            stmt.setTimestamp(4, Timestamp.valueOf(booking.getScheduledDateTime()));
            stmt.setString(5, booking.getStatus().name());
            stmt.setString(6, booking.getAddress());
            stmt.setDouble(7, booking.getTotalPrice());
            stmt.executeUpdate();
        }
    }

    public Booking findById(long id) throws SQLException {
        String sql = "SELECT id, customer_id, service_id, scheduled_datetime, status, address, total_price FROM bookings WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Booking b = new Booking(
                            rs.getLong("id"),
                            rs.getLong("customer_id"),
                            rs.getLong("service_id"),
                            rs.getTimestamp("scheduled_datetime").toLocalDateTime(),
                            rs.getString("address"),
                            rs.getDouble("total_price")
                    );
                    b.setStatus(BookingStatus.valueOf(rs.getString("status")));
                    return b;
                }
            }
        }
        return null;
    }

    public List<Booking> findByCustomerId(long customerId) throws SQLException {
        String sql = "SELECT id, customer_id, service_id, scheduled_datetime, status, address, total_price FROM bookings WHERE customer_id = ?";
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking b = new Booking(
                            rs.getLong("id"),
                            rs.getLong("customer_id"),
                            rs.getLong("service_id"),
                            rs.getTimestamp("scheduled_datetime").toLocalDateTime(),
                            rs.getString("address"),
                            rs.getDouble("total_price")
                    );
                    b.setStatus(BookingStatus.valueOf(rs.getString("status")));
                    bookings.add(b);
                }
            }
        }
        return bookings;
    }

    public List<Booking> findByHandymanId(long handymanId) throws SQLException {
        String sql = """
                SELECT b.id, b.customer_id, b.service_id, b.scheduled_datetime, b.status, b.address, b.total_price
                FROM bookings b
                JOIN services s ON b.service_id = s.id
                WHERE s.handyman_id = ?
                """;
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, handymanId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking b = new Booking(
                            rs.getLong("id"),
                            rs.getLong("customer_id"),
                            rs.getLong("service_id"),
                            rs.getTimestamp("scheduled_datetime").toLocalDateTime(),
                            rs.getString("address"),
                            rs.getDouble("total_price")
                    );
                    b.setStatus(BookingStatus.valueOf(rs.getString("status")));
                    bookings.add(b);
                }
            }
        }
        return bookings;
    }

    public boolean updateStatusIfOwnedByHandyman(long bookingId, long handymanId, BookingStatus newStatus) throws SQLException {
        String sql = """
                UPDATE bookings b
                JOIN services s ON b.service_id = s.id
                SET b.status = ?
                WHERE b.id = ? AND s.handyman_id = ?
                """;
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus.name());
            stmt.setLong(2, bookingId);
            stmt.setLong(3, handymanId);
            int updated = stmt.executeUpdate();
            return updated > 0;
        }
    }
}
