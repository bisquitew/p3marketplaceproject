package controller;

import app.MainFX;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.User;
import model.enums.UserRole;

public class CustomerDashboardController {

    @FXML private Label welcomeLabel;

    @FXML
    private void initialize() {
        User user = Session.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getFull_name() + " (" + user.getRole() + ")");
        }
    }

    @FXML
    private void onGoToServices() {
        MainFX.getSceneManager().showServicesScene();
    }

    @FXML
    private void onGoToBookings() {
        MainFX.getSceneManager().showBookingsScene();
    }

    @FXML
    private void onLogout() {
        Session.clear();
        MainFX.getSceneManager().showLoginScene();
    }

    @FXML
    private void onToggleDarkMode() {
        MainFX.getSceneManager().toggleDarkMode();
    }
}
