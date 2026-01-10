package repository;

import model.Service;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ServiceDAOTest {

    @Test
    void testFindServicesByCity() throws Exception {
        ServiceDAO dao = new ServiceDAO();

        Service s = new Service(
                9200L,
                1L,
                "Test Plumbing",
                "Fix pipes",
                200.0,
                "Plumbing",
                "Timisoara"
        );

        dao.insert(s);

        // Change findByFilters to search to match your DAO
        List<Service> results = dao.search("", "Timisoara");

        assertTrue(results.stream().anyMatch(x -> x.getId() == 9200L));
    }
}