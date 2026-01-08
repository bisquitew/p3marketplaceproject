package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    @Test
    void testReviewConstructor() {
        Review review = new Review(
                55L,
                100L,
                5,
                "Excellent service!"
        );

        assertEquals(55L, review.getId());
        assertEquals(100L, review.getBookingId());
        assertEquals(5, review.getRating());
        assertEquals("Excellent service!", review.getComment());
    }
}
