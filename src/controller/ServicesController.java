package controller;

import app.MainFX;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Booking;
import model.Service;
import model.User;
import repository.BookingDAO;
import repository.ServiceDAO;
import util.IdGenerator;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ServicesController {

    @FXML private TextField categoryField;
    @FXML private TextField cityField;
    @FXML private TableView<Service> servicesTable;
    @FXML private TableColumn<Service, Long> idColumn;
    @FXML private TableColumn<Service, String> titleColumn;
    @FXML private TableColumn<Service, String> cityColumn;
    @FXML private TableColumn<Service, String> categoryColumn;
    @FXML private TableColumn<Service, Double> priceColumn;

    @FXML private TextField bookingAddressField;
    @FXML private TextField bookingDateTimeField;
    @FXML private Label infoLabel;

    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final IdGenerator idGen = new IdGenerator(5000); // Unique starting point for GUI bookings

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getId()).asObject());
        titleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));
        cityColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCity()));
        categoryColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory()));
        priceColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrice()).asObject());

        loadAll();
    }

    private void loadAll() {
        try {
            List<Service> list = serviceDAO.findAllActive();
            servicesTable.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            infoLabel.setText("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void onSearch() {
        String category = categoryField.getText().trim();
        String city = cityField.getText().trim();
        try {
            List<Service> list = serviceDAO.search(category, city);
            servicesTable.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            infoLabel.setText("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void onBookService() {
        Service selected = servicesTable.getSelectionModel().getSelectedItem();
        User currentUser = Session.getCurrentUser();
        String address = bookingAddressField.getText().trim();
        String dateTimeStr = bookingDateTimeField.getText().trim();

        if (selected == null) {
            infoLabel.setText("Please select a service from the table first.");
            return;
        }
        if (currentUser == null) {
            infoLabel.setText("Session error: User not logged in.");
            return;
        }
        if (address.isEmpty() || dateTimeStr.isEmpty()) {
            infoLabel.setText("Address and Date-Time are required.");
            return;
        }

        try {
            LocalDateTime scheduledTime = LocalDateTime.parse(dateTimeStr);

            Booking newBooking = new Booking(
                    idGen.nextId(),
                    currentUser.getId(),
                    selected.getId(),
                    scheduledTime,
                    address,
                    selected.getPrice()
            );

            newBooking.validate();
            bookingDAO.insert(newBooking);

            infoLabel.setText("Success! Booking ID " + newBooking.getId() + " created.");
            bookingAddressField.clear();
            bookingDateTimeField.clear();

        } catch (java.time.format.DateTimeParseException e) {
            infoLabel.setText("Invalid date format. Use YYYY-MM-DDTHH:MM (e.g., 2026-01-15T10:00).");
        } catch (Exception e) {
            infoLabel.setText("Booking failed: " + e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        MainFX.getSceneManager().showCustomerDashboard();
    }

    @FXML
    private void onToggleDarkMode() {
        MainFX.getSceneManager().toggleDarkMode();
    }
}