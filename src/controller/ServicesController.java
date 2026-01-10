package controller;

import app.MainFX;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Booking;
import model.Service;
import model.enums.BookingStatus;
import model.exceptions.InvalidDataException;
import repository.BookingDAO;
import repository.ServiceDAO;
import util.IdGenerator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ServicesController {

    @FXML
    private TextField categoryField, cityField;

    @FXML
    private TableView<Service> servicesTable;

    @FXML
    private TableColumn<Service, Long> idColumn;

    @FXML
    private TableColumn<Service, String> titleColumn, cityColumn;

    @FXML
    private TableColumn<Service, Double> priceColumn;

    @FXML
    private Label infoLabel;

    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final IdGenerator idGen = new IdGenerator(2000);

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getId()).asObject());
        titleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));
        cityColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCity()));
        priceColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrice()).asObject());

        loadServices(null, null);
    }

    private void loadServices(String category, String city) {
        try {
            List<Service> services = serviceDAO.findByFilters(category, city);
            servicesTable.setItems(FXCollections.observableArrayList(services));
        } catch (SQLException e) {
            infoLabel.setText("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void onSearch() {
        String category = categoryField.getText().isBlank() ? null : categoryField.getText();
        String city = cityField.getText().isBlank() ? null : cityField.getText();
        loadServices(category, city);
    }

    @FXML
    private void onMakeBooking() {
        Service selected = servicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            infoLabel.setText("Select a service first.");
            return;
        }

        var user = Session.getCurrentUser();
        if (user == null) {
            infoLabel.setText("You must be logged in as a customer.");
            return;
        }

        // Address
        TextInputDialog addressDialog = new TextInputDialog();
        addressDialog.setHeaderText("Enter booking address:");
        addressDialog.setContentText("Address:");
        var addressResult = addressDialog.showAndWait();
        if (addressResult.isEmpty()) return;
        String address = addressResult.get();

        // Date
        TextInputDialog dateDialog = new TextInputDialog(LocalDate.now().plusDays(1).toString());
        dateDialog.setHeaderText("Enter booking date:");
        dateDialog.setContentText("Date (YYYY-MM-DD):");
        var dateOpt = dateDialog.showAndWait();
        if (dateOpt.isEmpty()) return;

        LocalDate date;
        try {
            date = LocalDate.parse(dateOpt.get());
        } catch (Exception e) {
            infoLabel.setText("Invalid date format.");
            return;
        }

        // Time
        TextInputDialog timeDialog = new TextInputDialog("10:00");
        timeDialog.setHeaderText("Enter booking time:");
        timeDialog.setContentText("Time (HH:MM):");
        var timeOpt = timeDialog.showAndWait();
        if (timeOpt.isEmpty()) return;

        LocalTime time;
        try {
            time = LocalTime.parse(timeOpt.get());
        } catch (Exception e) {
            infoLabel.setText("Invalid time format.");
            return;
        }

        LocalDateTime dateTime = LocalDateTime.of(date, time);

        try {
            Booking booking = new Booking(
                    idGen.nextId(),
                    user.getId(),
                    selected.getId(),
                    dateTime,
                    address,
                    selected.getPrice()
            );
            booking.setStatus(BookingStatus.PENDING);
            booking.validate();
            bookingDAO.insert(booking);
            infoLabel.setText("Booking created.");
        } catch (InvalidDataException e) {
            infoLabel.setText("Validation error: " + e.getMessage());
        } catch (Exception e) {
            infoLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        MainFX.getSceneManager().showCustomerDashboard();
    }
}
