package controller;

import app.MainFX;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Booking;
import model.enums.BookingStatus;
import repository.BookingDAO;

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

        if (selected.getStatus() != BookingStatus.COMPLETED) {
            infoLabel.setText("You can only review completed bookings.");
            return;
        }

        infoLabel.setText("Review feature not implemented yet.");
    }

    @FXML
    private void onBack() {
        MainFX.getSceneManager().showCustomerDashboard();
    }
}
