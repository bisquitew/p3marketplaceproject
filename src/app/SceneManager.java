package app;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class SceneManager {

    private final Stage stage;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    private void switchScene(String fxml, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/" + fxml));
            Scene scene = new Scene(root);

            // Global CSS
            scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Handyman Marketplace - " + title);

            // Fade-in animation
            FadeTransition ft = new FadeTransition(Duration.millis(300), root);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showLoginScene() { switchScene("login.fxml", "Login"); }
    public void showRegisterScene() { switchScene("register.fxml", "Register"); }
    public void showCustomerDashboard() { switchScene("customer_dashboard.fxml", "Dashboard"); }
    public void showServicesScene() { switchScene("services.fxml", "Services"); }
    public void showBookingsScene() { switchScene("bookings.fxml", "My Bookings"); }
}
