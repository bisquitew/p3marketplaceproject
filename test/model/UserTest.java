package model;

import model.enums.UserRole;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserConstructor() {
        User user = new User(
                1L,
                "john123",
                "pass123",
                "John Doe",
                "john@mail.com",
                UserRole.CUSTOMER
        );

        assertEquals(1L, user.getId());
        assertEquals("john123", user.getUsername());
        assertEquals("pass123", user.getPassword());
        assertEquals("John Doe", user.getFullName());
        assertEquals("john@mail.com", user.getEmail());
        assertEquals(UserRole.CUSTOMER, user.getRole());
    }
}
