package controller;

import app.MainFX;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Booking;
import model.Review;
import model.Service;
import model.enums.BookingStatus;
import model.exceptions.InvalidDataException;
import repository.BookingDAO;
import repository.ReviewDAO;
import repository.ServiceDAO;
import repository.UserDAO;
import util.IdGenerator;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookingsController {

    @FXML
    private TableView<Booking> bookingsTable;

    @FXML
    private TableColumn<Booking, Long> idColumn;

    @FXML
    private TableColumn<Booking, Long> serviceColumn;

    @FXML
    private TableColumn<Booking, String> dateColumn;

    @FXML
    private TableColumn<Booking, String> statusColumn;

    @FXML
    private TableColumn<Booking, String> addressColumn;

    @FXML
    private TableColumn<Booking, Double> priceColumn;

    @FXML
    private Label infoLabel;

    private final BookingDAO bookingDAO = new BookingDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final UserDAO userDAO = new UserDAO();
    private final IdGenerator idGen = new IdGenerator(3000);

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getId()).asObject());
        serviceColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getServiceId()).asObject());
        dateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getScheduledDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        ));
        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().toString()));
        addressColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAddress()));
        priceColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getTotalPrice()).asObject());

        loadBookings();
    }

    private void loadBookings() {
        try {
            long userId = Session.getCurrentUser().getId();
            List<Booking> list = bookingDAO.findByCustomerId(userId);
            bookingsTable.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            infoLabel.setText("Database error: " + e.getMessage());
        } catch (NullPointerException e) {
            infoLabel.setText("No logged-in user.");
        }
    }

    @FXML
    private void onRefresh() {
        loadBookings();
    }

    @FXML
    private void onAddReview() {
        Booking selected = bookingsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            infoLabel.setText("Select a booking first.");
            return;
        }

        if (selected.getStatus() != BookingStatus.COMPLETED &&
                selected.getStatus() != BookingStatus.CANCELLED) {
            infoLabel.setText("You can review only completed or cancelled bookings.");
            return;
        }

        // Rating
        TextInputDialog ratingDialog = new TextInputDialog("5");
        ratingDialog.setHeaderText("Rate this booking (1-5):");
        ratingDialog.setContentText("Rating:");
        var ratingOpt = ratingDialog.showAndWait();
        if (ratingOpt.isEmpty()) return;

        int rating;
        try {
            rating = Integer.parseInt(ratingOpt.get());
            if (rating < 1 || rating > 5) {
                infoLabel.setText("Rating must be between 1 and 5.");
                return;
            }
        } catch (NumberFormatException e) {
            infoLabel.setText("Invalid rating value.");
            return;
        }

        // Comment
        TextInputDialog commentDialog = new TextInputDialog();
        commentDialog.setHeaderText("Leave a comment (optional):");
        commentDialog.setContentText("Comment:");
        var commentOpt = commentDialog.showAndWait();
        if (commentOpt.isEmpty()) return;
        String comment = commentOpt.get();

        try {
            Review review = new Review(idGen.nextId(), selected.getId(), rating, comment);
            review.validate();
            reviewDAO.insert(review);

            // Update handyman rating
            Service s = serviceDAO.findById(selected.getServiceId());
            if (s != null) {
                long handymanId = s.getHandymanId();
                double avg = reviewDAO.calculateHandymanAverageRating(handymanId);
                userDAO.updateRating(handymanId, avg);
            }

            infoLabel.setText("Review saved. Thank you!");
        } catch (InvalidDataException e) {
            infoLabel.setText("Validation error: " + e.getMessage());
        } catch (SQLException e) {
            infoLabel.setText("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        MainFX.getSceneManager().showCustomerDashboard();
    }
}
