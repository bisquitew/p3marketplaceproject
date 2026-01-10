package controller;

import app.MainFX;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Booking;
import model.Service;
import model.User;
import model.enums.BookingStatus;
import model.exceptions.InvalidDataException;
import repository.BookingDAO;
import repository.ReviewDAO;
import repository.ServiceDAO;
import repository.UserDAO;
import util.IdGenerator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HandymanDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label ratingLabel;
    @FXML private Label ratingBreakdownLabel;
    @FXML private Label infoLabel;

    // Bookings table
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, Long> idColumn;
    @FXML private TableColumn<Booking, Long> customerColumn;
    @FXML private TableColumn<Booking, String> dateColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    @FXML private TableColumn<Booking, String> addressColumn;
    @FXML private TableColumn<Booking, Double> priceColumn;

    // Services table
    @FXML private TableView<Service> servicesTable;
    @FXML private TableColumn<Service, Long> serviceIdColumn;
    @FXML private TableColumn<Service, String> serviceTitleColumn;
    @FXML private TableColumn<Service, String> serviceCityColumn;
    @FXML private TableColumn<Service, String> serviceCategoryColumn;
    @FXML private TableColumn<Service, Double> servicePriceColumn;
    @FXML private TableColumn<Service, String> serviceActiveColumn;

    // New service creation fields
    @FXML private TextField newServiceTitleField;
    @FXML private TextField newServiceCityField;
    @FXML private TextField newServicePriceField;
    @FXML private TextField newServiceCategoryField;
    @FXML private TextArea newServiceDescriptionArea;

    private final BookingDAO bookingDAO = new BookingDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final UserDAO userDAO = new UserDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final IdGenerator idGen = new IdGenerator(4000);

    @FXML
    private void initialize() {
        User u = Session.getCurrentUser();
        if (u != null) {
            welcomeLabel.setText("Welcome, " + u.getFull_name() + " (" + u.getRole() + ")");
            ratingLabel.setText("Average rating: " + u.getRating());
        }

        // Bookings table bindings
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getId()).asObject());
        customerColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getCustomerId()).asObject());
        dateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getScheduledDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        ));
        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().toString()));
        addressColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAddress()));
        priceColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getTotalPrice()).asObject());

        // Services table bindings
        serviceIdColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getId()).asObject());
        serviceTitleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));
        serviceCityColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCity()));
        serviceCategoryColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory()));
        servicePriceColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        serviceActiveColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().isActive() ? "Active" : "Inactive"));

        loadData();
    }

    private void loadData() {
        try {
            long handymanId = Session.getCurrentUser().getId();

            // Load bookings
            List<Booking> bookings = bookingDAO.findByHandymanId(handymanId);
            bookingsTable.setItems(FXCollections.observableArrayList(bookings));

            // Load services
            List<Service> services = serviceDAO.findByHandyman(handymanId);
            servicesTable.setItems(FXCollections.observableArrayList(services));

            // Refresh rating
            User refreshed = userDAO.findById(handymanId);
            if (refreshed != null) {
                ratingLabel.setText("Average rating: " + refreshed.getRating());
            }

            // Rating breakdown
            int[] breakdown = reviewDAO.getRatingBreakdownForHandyman(handymanId);
            ratingBreakdownLabel.setText(
                    "Ratings: 5★ " + breakdown[4] +
                            " | 4★ " + breakdown[3] +
                            " | 3★ " + breakdown[2] +
                            " | 2★ " + breakdown[1] +
                            " | 1★ " + breakdown[0]
            );

        } catch (SQLException e) {
            infoLabel.setText("Database error: " + e.getMessage());
        }
    }

    private Booking getSelectedBooking() {
        Booking b = bookingsTable.getSelectionModel().getSelectedItem();
        if (b == null) infoLabel.setText("Select a booking first.");
        return b;
    }

    private boolean isCompleted(Booking b) {
        if (b.getStatus() == BookingStatus.COMPLETED) {
            infoLabel.setText("Completed bookings cannot be modified.");
            return true;
        }
        return false;
    }

    @FXML
    private void onAccept() {
        Booking b = getSelectedBooking();
        if (b == null || isCompleted(b)) return;
        updateStatus(b.getId(), BookingStatus.ACCEPTED);
    }

    @FXML
    private void onComplete() {
        Booking b = getSelectedBooking();
        if (b == null || isCompleted(b)) return;
        updateStatus(b.getId(), BookingStatus.COMPLETED);
    }

    @FXML
    private void onCancel() {
        Booking b = getSelectedBooking();
        if (b == null || isCompleted(b)) return;
        updateStatus(b.getId(), BookingStatus.CANCELLED);
    }

    private void updateStatus(long bookingId, BookingStatus status) {
        try {
            long handymanId = Session.getCurrentUser().getId();
            boolean updated = bookingDAO.updateStatusIfOwnedByHandyman(bookingId, handymanId, status);

            if (updated) {
                infoLabel.setText("Booking status updated to " + status);
                loadData();
            } else {
                infoLabel.setText("Booking not found or not assigned to you.");
            }

        } catch (SQLException e) {
            infoLabel.setText("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void onReschedule() {
        Booking b = getSelectedBooking();
        if (b == null || isCompleted(b)) return;

        // Ask for date
        TextInputDialog dateDialog = new TextInputDialog(b.getScheduledDateTime().toLocalDate().toString());
        dateDialog.setHeaderText("Enter new date (YYYY-MM-DD)");
        var dateOpt = dateDialog.showAndWait();
        if (dateOpt.isEmpty()) return;

        LocalDate date;
        try {
            date = LocalDate.parse(dateOpt.get());
        } catch (Exception e) {
            infoLabel.setText("Invalid date format.");
            return;
        }

        // Ask for time
        TextInputDialog timeDialog = new TextInputDialog(b.getScheduledDateTime().toLocalTime().toString().substring(0, 5));
        timeDialog.setHeaderText("Enter new time (HH:MM)");
        var timeOpt = timeDialog.showAndWait();
        if (timeOpt.isEmpty()) return;

        LocalTime time;
        try {
            time = LocalTime.parse(timeOpt.get());
        } catch (Exception e) {
            infoLabel.setText("Invalid time format.");
            return;
        }

        LocalDateTime newDateTime = LocalDateTime.of(date, time);

        try {
            long handymanId = Session.getCurrentUser().getId();
            boolean updated = bookingDAO.rescheduleIfOwnedByHandyman(
                    b.getId(),
                    handymanId,
                    java.sql.Timestamp.valueOf(newDateTime)
            );

            if (updated) {
                infoLabel.setText("Booking rescheduled.");
                loadData();
            } else {
                infoLabel.setText("Booking not found or not assigned to you.");
            }

        } catch (SQLException e) {
            infoLabel.setText("Database error: " + e.getMessage());
        }
    }

    // ============================
    //      ADD NEW SERVICE
    // ============================

    @FXML
    private void onAddService() {
        infoLabel.setText("");

        String title = newServiceTitleField.getText();
        String city = newServiceCityField.getText();
        String priceStr = newServicePriceField.getText();
        String category = newServiceCategoryField.getText();
        String description = newServiceDescriptionArea.getText();

        if (title.isBlank() || city.isBlank() || priceStr.isBlank() || category.isBlank()) {
            infoLabel.setText("Title, city, price, and category are required.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            infoLabel.setText("Price must be a valid number.");
            return;
        }

        try {
            long handymanId = Session.getCurrentUser().getId();

            Service service = new Service(
                    idGen.nextId(),
                    handymanId,
                    title,
                    description,
                    price,
                    category,
                    city
            );

            service.validate();
            serviceDAO.insert(service);

            newServiceTitleField.clear();
            newServiceCityField.clear();
            newServicePriceField.clear();
            newServiceCategoryField.clear();
            newServiceDescriptionArea.clear();

            infoLabel.setText("Service created successfully.");
            loadData();

        } catch (InvalidDataException e) {
            infoLabel.setText("Validation error: " + e.getMessage());
        } catch (SQLException e) {
            infoLabel.setText("Database error while adding service: " + e.getMessage());
        }
    }

    @FXML
    private void onToggleServiceActive() {
        Service selected = servicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            infoLabel.setText("Select a service first.");
            return;
        }

        try {
            long handymanId = Session.getCurrentUser().getId();
            boolean toggled = serviceDAO.toggleActiveForHandyman(selected.getId(), handymanId);

            if (toggled) {
                infoLabel.setText("Service active state toggled.");
                loadData();
            } else {
                infoLabel.setText("Service not found or not owned by you.");
            }

        } catch (SQLException e) {
            infoLabel.setText("Database error: " + e.getMessage());
        }
    }

    // ============================
    //      NAVIGATION / THEME
    // ============================

    @FXML
    private void onBackToLogin() {
        Session.clear();
        MainFX.getSceneManager().showLoginScene();
    }

    @FXML
    private void onToggleDarkMode() {
        MainFX.getSceneManager().toggleDarkMode();
    }
}
