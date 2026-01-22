package controller;

import app.MainFX;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Booking;
import model.Service;
import model.User;
import model.enums.BookingStatus;
import repository.BookingDAO;
import repository.ReviewDAO;
import repository.ServiceDAO;
import repository.UserDAO;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class HandymanDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label ratingLabel;
    @FXML private Label ratingBreakdownLabel;
    @FXML private Label infoLabel;

    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, Long> idColumn;
    @FXML private TableColumn<Booking, String> customerColumn;
    @FXML private TableColumn<Booking, String> dateColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    @FXML private TableColumn<Booking, String> addressColumn;
    @FXML private TableColumn<Booking, Void> actionColumn; // NEW Chat

    @FXML private TableView<Service> servicesTable;
    @FXML private TableColumn<Service, String> serviceTitleColumn;
    @FXML private TableColumn<Service, String> serviceCityColumn;
    @FXML private TableColumn<Service, Double> servicePriceColumn;
    @FXML private TableColumn<Service, String> serviceActiveColumn;

    private final BookingDAO bookingDAO = new BookingDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final UserDAO userDAO = new UserDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadData();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleLongProperty(d.getValue().getId()).asObject());

        customerColumn.setCellValueFactory(d -> {
            try {
                User u = userDAO.findById(d.getValue().getCustomerId());
                return new javafx.beans.property.SimpleStringProperty(u != null ? u.getFull_name() : "Unknown");
            } catch (SQLException e) {
                return new javafx.beans.property.SimpleStringProperty("Error");
            }
        });

        dateColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getScheduledDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        statusColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getStatus().toString()));
        addressColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getAddress()));

        // Chat Button Column
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Chat");
            {
                btn.getStyleClass().add("button");
                btn.setStyle("-fx-font-size: 10px; -fx-padding: 5 10;");
                btn.setOnAction(event -> {
                    Booking b = getTableView().getItems().get(getIndex());
                    MainFX.getSceneManager().showChatScene(b.getId());
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Services
        serviceTitleColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTitle()));
        serviceCityColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCity()));
        servicePriceColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getPrice()).asObject());
        serviceActiveColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().isActive() ? "Active" : "Inactive"));
    }

    private void loadData() {
        try {
            User u = Session.getCurrentUser();
            if (u == null) return;

            welcomeLabel.setText("Welcome back, " + u.getFull_name());

            long handymanId = u.getId();
            bookingsTable.setItems(FXCollections.observableArrayList(bookingDAO.findByHandymanId(handymanId)));
            servicesTable.setItems(FXCollections.observableArrayList(serviceDAO.findByHandyman(handymanId)));

            double avg = reviewDAO.calculateHandymanAverageRating(handymanId);
            ratingLabel.setText(String.format("%.1f ★", avg));

            int[] breakdown = reviewDAO.getRatingBreakdownForHandyman(handymanId);
            ratingBreakdownLabel.setText(String.format("5★:%d  4★:%d  3★:%d  2★:%d  1★:%d",
                    breakdown[4], breakdown[3], breakdown[2], breakdown[1], breakdown[0]));
        } catch (SQLException e) {
            infoLabel.setText("Error loading data: " + e.getMessage());
        }
    }

    @FXML private void onAccept() { updateStatus(BookingStatus.ACCEPTED); }
    @FXML private void onComplete() { updateStatus(BookingStatus.COMPLETED); }
    @FXML private void onReschedule() { infoLabel.setText("Reschedule feature coming soon."); }

    private void updateStatus(BookingStatus status) {
        Booking selected = bookingsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            infoLabel.setText("Please select a booking.");
            return;
        }
        try {
            boolean success = bookingDAO.updateStatusIfOwnedByHandyman(selected.getId(), Session.getCurrentUser().getId(), status);
            if (success) {
                loadData();
                infoLabel.setText("Booking updated successfully.");
            } else {
                infoLabel.setText("Update failed. You may not own this booking.");
            }
        } catch (SQLException e) {
            infoLabel.setText("Update failed: " + e.getMessage());
        }
    }

    @FXML
    private void onToggleServiceActive() {
        Service selected = servicesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                serviceDAO.toggleActiveForHandyman(selected.getId(), Session.getCurrentUser().getId());
                loadData();
            } catch (SQLException e) {
                infoLabel.setText("Toggle failed.");
            }
        } else {
            infoLabel.setText("Select a service to toggle.");
        }
    }

    @FXML void onToggleDarkMode() { MainFX.getSceneManager().toggleDarkMode(); }
    @FXML void onBackToLogin() { Session.clear(); MainFX.getSceneManager().showLoginScene(); }
}