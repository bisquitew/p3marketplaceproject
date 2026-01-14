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

    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, Long> idColumn;
    @FXML private TableColumn<Booking, Long> serviceColumn;
    @FXML private TableColumn<Booking, String> dateColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    @FXML private TableColumn<Booking, String> addressColumn;
    @FXML private TableColumn<Booking, Double> priceColumn;
    @FXML private Label infoLabel;

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
            if (Session.getCurrentUser() == null) {
                infoLabel.setText("No logged-in user.");
                return;
            }
            long userId = Session.getCurrentUser().getId();
            List<Booking> list = bookingDAO.findByCustomerId(userId);
            bookingsTable.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            infoLabel.setText("Database error: " + e.getMessage());
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

        if (selected.getStatus() != BookingStatus.COMPLETED) {
            infoLabel.setText("You can review only completed bookings.");
            return;
        }

        TextInputDialog ratingDialog = new TextInputDialog("5");
        ratingDialog.setTitle("Add Review");
        ratingDialog.setHeaderText("Rate service (1-5)");
        ratingDialog.showAndWait().ifPresent(r -> {
            try {
                int rating = Integer.parseInt(r);
                TextInputDialog commentDialog = new TextInputDialog("");
                commentDialog.setTitle("Add Review");
                commentDialog.setHeaderText("Comment");
                commentDialog.showAndWait().ifPresent(comment -> {
                    try {
                        Review review = new Review(idGen.nextId(), selected.getId(), rating, comment);
                        review.validate();
                        reviewDAO.insert(review);

                        Service s = serviceDAO.findById(selected.getServiceId());
                        if (s != null) {
                            double avg = reviewDAO.calculateHandymanAverageRating(s.getHandymanId());
                            userDAO.updateRating(s.getHandymanId(), avg);
                        }
                        infoLabel.setText("Review added successfully.");
                    } catch (Exception e) {
                        infoLabel.setText("Error: " + e.getMessage());
                    }
                });
            } catch (NumberFormatException e) {
                infoLabel.setText("Invalid rating.");
            }
        });
    }

    @FXML
    private void onToggleDarkMode() {
        MainFX.getSceneManager().toggleDarkMode();
    }

    @FXML
    private void onBack() {
        MainFX.getSceneManager().showCustomerDashboard();
    }
}