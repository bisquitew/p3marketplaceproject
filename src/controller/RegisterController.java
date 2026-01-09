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

    @FXML
    private TextField usernameField, fullNameField, emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleCombo;

    @FXML
    private Label errorLabel;

    private final UserDAO userDAO = new UserDAO();
    private final IdGenerator idGen = new IdGenerator(1000);

    @FXML
    private void initialize() {
        roleCombo.getItems().addAll("CUSTOMER", "HANDYMAN");
    }

    @FXML
    private void onRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String roleStr = roleCombo.getValue();

        if (roleStr == null) {
            errorLabel.setText("Select a role.");
            return;
        }

        try {
            UserRole role = UserRole.valueOf(roleStr);
            User user = new User(idGen.nextId(), username, password, fullName, email, role);
            user.validate();
            userDAO.insert(user);
            errorLabel.setText("Registered successfully. You can login now.");
        } catch (IllegalArgumentException e) {
            errorLabel.setText("Invalid role.");
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
