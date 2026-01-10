package controller;

import app.MainFX;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Service;
import repository.ServiceDAO;

import java.sql.SQLException;
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

    @FXML private Label infoLabel;

    private final ServiceDAO serviceDAO = new ServiceDAO();

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
    private void onBack() {
        MainFX.getSceneManager().showCustomerDashboard();
    }

    @FXML
    private void onToggleDarkMode() {
        MainFX.getSceneManager().toggleDarkMode();
    }
}
