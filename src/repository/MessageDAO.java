package repository;

import model.Message;
import util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public void insert(Message message) throws SQLException {
        String sql = "INSERT INTO messages (id, booking_id, sender_user_id, text, sent_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, message.getId());
            stmt.setLong(2, message.getBookingId());
            stmt.setLong(3, message.getSenderUserId());
            stmt.setString(4, message.getText());
            stmt.setTimestamp(5, Timestamp.valueOf(message.getSentAt()));
            stmt.executeUpdate();
        }
    }

    public List<Message> findByBookingId(long bookingId) throws SQLException {
        String sql = "SELECT * FROM messages WHERE booking_id = ? ORDER BY sent_at ASC";
        List<Message> list = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Message m = new Message(
                            rs.getLong("id"),
                            rs.getLong("booking_id"),
                            rs.getLong("sender_user_id"),
                            rs.getString("text"),
                            rs.getTimestamp("sent_at").toLocalDateTime()
                    );
                    list.add(m);
                }
            }
        }
        return list;
    }
}