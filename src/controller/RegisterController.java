package controller;

import app.MainFX;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.User;
import model.enums.UserRole;
import model.exceptions.InvalidDataException;
import repository.UserDAO;
import util.IdGenerator;

import java.sql.SQLException;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Label errorLabel;

    private final UserDAO userDAO = new UserDAO();
    private final IdGenerator idGen = new IdGenerator(1000);

    @FXML
    private void initialize() {
        roleCombo.getItems().addAll("CUSTOMER", "HANDYMAN");
    }

    @FXML
    private void onRegister() {
        try {
            String roleStr = roleCombo.getValue();
            if (roleStr == null) {
                errorLabel.setText("Please select a role.");
                return;
            }

            UserRole role = UserRole.valueOf(roleStr);
            User user = new User(
                    idGen.nextId(),
                    usernameField.getText(),
                    passwordField.getText(),
                    fullNameField.getText(),
                    emailField.getText(),
                    role
            );

            user.validate();
            userDAO.insert(user);

            errorLabel.setText("Registered successfully!");

        } catch (InvalidDataException e) {
            errorLabel.setText("Validation error: " + e.getMessage());
        } catch (SQLException e) {
            errorLabel.setText("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        MainFX.getSceneManager().showLoginScene();
    }
}
