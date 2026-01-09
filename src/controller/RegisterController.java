package controller;

import app.MainFX;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.User;
import model.enums.UserRole;
import model.exceptions.InvalidDataException;
import repository.UserDAO;

import java.sql.SQLException;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Label errorLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void initialize() {
        roleCombo.getItems().addAll("CUSTOMER", "HANDYMAN");
    }

    @FXML
    private void onRegister() {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String fullName = fullNameField.getText();
            String email = emailField.getText();
            String roleStr = roleCombo.getValue();

            if (roleStr == null) {
                errorLabel.setText("Please select a role.");
                return;
            }

            UserRole role = UserRole.valueOf(roleStr);

            // Let DB generate the ID
            User user = new User(username, password, fullName, email, role);

            user.validate();
            userDAO.insert(user);

            errorLabel.setText("Registered successfully! You can now log in.");

        } catch (InvalidDataException e) {
            errorLabel.setText("Validation error: " + e.getMessage());
        } catch (SQLException e) {
            errorLabel.setText("Database error: " + e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("Unexpected error: " + e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        MainFX.getSceneManager().showLoginScene();
    }
}
