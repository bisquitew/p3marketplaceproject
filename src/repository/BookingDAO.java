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

    public List<Booking> findAll() throws SQLException {
        String sql = "SELECT id, customer_id, service_id, scheduled_datetime, status, address, total_price FROM bookings";
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

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
        return bookings;
    }
}
