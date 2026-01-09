package controller;

import app.MainFX;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.User;

public class CustomerDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private void initialize() {
        User u = Session.getCurrentUser();
        if (u != null) {
            welcomeLabel.setText("Welcome, " + u.getFullName() + " (" + u.getRole() + ")");
        } else {
            welcomeLabel.setText("Welcome, customer");
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
}
