package repository;

import model.Booking;
import model.enums.BookingStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingDAOTest {

    @Test
    void testInsertAndFindBooking() throws Exception {
        BookingDAO dao = new BookingDAO();

        Booking b = new Booking(
                9100L,
                1L,
                2L,
                LocalDateTime.now(),
                "Test Address",
                150.0
        );

        dao.insert(b);

        Booking found = dao.findById(9100L);

        assertNotNull(found);
        assertEquals(1L, found.getCustomerId());
        assertEquals(2L, found.getServiceId());
        assertEquals("Test Address", found.getAddress());
        assertEquals(BookingStatus.PENDING, found.getStatus());
    }
}
