package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private long bookingId;
    private long senderUserId;
    private String text;
    private LocalDateTime sentAt;

    public Message() {}

    public Message(long id, long bookingId, long senderUserId, String text, LocalDateTime sentAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.senderUserId = senderUserId;
        this.text = text;
        this.sentAt = sentAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getBookingId() { return bookingId; }
    public void setBookingId(long bookingId) { this.bookingId = bookingId; }

    public long getSenderUserId() { return senderUserId; }
    public void setSenderUserId(long senderUserId) { this.senderUserId = senderUserId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}
