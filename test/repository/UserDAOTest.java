package repository;

import model.User;
import model.enums.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    @Test
    void testLoginSuccess() throws Exception {
        UserDAO dao = new UserDAO();

        User u = new User(
                9001L,
                "testLoginUser",
                "pass123",
                "Test User",
                "test@login.com",
                UserRole.CUSTOMER,
                0.0
        );
        dao.insert(u);

        User found = dao.findByUsernameAndPassword("testLoginUser", "pass123");

        assertNotNull(found);
        assertEquals("testLoginUser", found.getUsername());
        assertEquals("Test User", found.getFull_name());
    }
}
