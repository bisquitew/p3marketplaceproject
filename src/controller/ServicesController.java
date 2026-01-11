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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

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
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> hourCombo;
    @FXML private ComboBox<String> minuteCombo;
    @FXML private Label infoLabel;

    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final IdGenerator idGen = new IdGenerator(5000);

    @FXML
    private void initialize() {
        // Table Bindings
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getId()).asObject());
        titleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));
        cityColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCity()));
        categoryColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory()));
        priceColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrice()).asObject());

        // Initialize Dropdown values
        for (int i = 0; i < 24; i++) hourCombo.getItems().add(String.format("%02d", i));
        for (int i = 0; i < 60; i += 5) minuteCombo.getItems().add(String.format("%02d", i)); // 5-minute intervals

        hourCombo.getSelectionModel().select("12");
        minuteCombo.getSelectionModel().select("00");
        datePicker.setValue(LocalDate.now().plusDays(1));

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
        LocalDate date = datePicker.getValue();
        String hour = hourCombo.getValue();
        String min = minuteCombo.getValue();

        if (selected == null) {
            infoLabel.setText("Please select a service first.");
            return;
        }
        if (currentUser == null) {
            infoLabel.setText("User session not found.");
            return;
        }
        if (address.isEmpty() || date == null || hour == null || min == null) {
            infoLabel.setText("All booking fields are required.");
            return;
        }

        try {
            LocalDateTime scheduledTime = LocalDateTime.of(date, LocalTime.of(Integer.parseInt(hour), Integer.parseInt(min)));

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

            infoLabel.setText("Booking confirmed! ID: " + newBooking.getId());
            bookingAddressField.clear();

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