package controller;

import app.MainFX;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;
import repository.UserDAO;

import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void onLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            User user = userDAO.findByUsernameAndPassword(username, password);
            if (user != null) {
                Session.setCurrentUser(user);
                switch (user.getRole()) {
                    case CUSTOMER -> MainFX.getSceneManager().showCustomerDashboard();
                    case HANDYMAN -> MainFX.getSceneManager().showCustomerDashboard(); // later: handyman dashboard
                    case ADMIN -> MainFX.getSceneManager().showCustomerDashboard();    // later: admin dashboard
                }
            } else {
                errorLabel.setText("Invalid credentials.");
            }
        } catch (SQLException e) {
            errorLabel.setText("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void onGoToRegister() {
        MainFX.getSceneManager().showRegisterScene();
    }
}
