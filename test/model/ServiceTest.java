package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {

    @Test
    void testServiceConstructor() {
        Service service = new Service(
                10L,
                5L,
                "Fix Pipe",
                "Professional plumbing service",
                150.0,
                "Plumbing",
                "Cluj-Napoca"
        );

        assertEquals(10L, service.getId());
        assertEquals(5L, service.getHandymanId());
        assertEquals("Fix Pipe", service.getTitle());
        assertEquals("Professional plumbing service", service.getDescription());
        assertEquals(150.0, service.getPrice());
        assertEquals("Plumbing", service.getCategory());
        assertEquals("Cluj-Napoca", service.getCity());
        assertTrue(service.isActive());
    }
}
