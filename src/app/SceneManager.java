package app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    private final Stage stage;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    private void switchScene(String fxml, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/" + fxml));
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showLoginScene() {
        switchScene("login.fxml", "Login - Handyman Marketplace");
    }

    public void showRegisterScene() {
        switchScene("register.fxml", "Register - Handyman Marketplace");
    }

    public void showCustomerDashboard() {
        switchScene("customer_dashboard.fxml", "Customer Dashboard");
    }

    public void showServicesScene() {
        switchScene("services.fxml", "Services");
    }

    public void showBookingsScene() {
        switchScene("bookings.fxml", "My Bookings");
    }
}
