package app;

import model.Booking;
import model.Review;
import model.Service;
import model.User;
import repository.BookingDAO;
import repository.InMemoryDatabase;
import repository.ReviewDAO;
import repository.ServiceDAO;
import repository.UserDAO;

public class DatabaseSyncService {

    private final InMemoryDatabase memoryDb;
    private final UserDAO userDAO;
    private final ServiceDAO serviceDAO;
    private final BookingDAO bookingDAO;
    private final ReviewDAO reviewDAO;

    public DatabaseSyncService(InMemoryDatabase memoryDb) {
        this.memoryDb = memoryDb;
        this.userDAO = new UserDAO();
        this.serviceDAO = new ServiceDAO();
        this.bookingDAO = new BookingDAO();
        this.reviewDAO = new ReviewDAO();
    }

    public void syncAllToMySQL() {
        try {
            syncUsers();
            syncServices();
            syncBookings();
            syncReviews();
            System.out.println("Sync to MySQL completed successfully.");
        } catch (Exception e) {
            System.err.println("Sync to MySQL failed: " + e.getMessage());
        }
    }

    private void syncUsers() throws Exception {
        for (User u : memoryDb.getUsers().values()) {
            userDAO.insert(u);
        }
    }

    private void syncServices() throws Exception {
        for (Service s : memoryDb.getServices().values()) {
            serviceDAO.insert(s);
        }
    }

    private void syncBookings() throws Exception {
        for (Booking b : memoryDb.getBookings().values()) {
            bookingDAO.insert(b);
        }
    }

    private void syncReviews() throws Exception {
        for (Review r : memoryDb.getReviews().values()) {
            reviewDAO.insert(r);
        }
    }
}
