package model;

import model.enums.BookingStatus;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void testBookingConstructor() {
        LocalDateTime now = LocalDateTime.now();

        Booking booking = new Booking(
                100L,
                20L,
                30L,
                now,
                "Strada Mare 10",
                200.0
        );

        assertEquals(100L, booking.getId());
        assertEquals(20L, booking.getCustomerId());
        assertEquals(30L, booking.getServiceId());
        assertEquals(now, booking.getScheduledDateTime());
        assertEquals("Strada Mare 10", booking.getAddress());
        assertEquals(200.0, booking.getTotalPrice());
        assertEquals(BookingStatus.PENDING, booking.getStatus());
    }
}
