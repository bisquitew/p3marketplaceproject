package app;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneManager {

    private final Stage stage;
    private boolean darkMode = false;
    private String currentFxml = null;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    private void switchScene(String fxml, String title) {
        try {
            currentFxml = fxml; // track current scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxml));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());
            if (darkMode) root.getStyleClass().add("dark");

            stage.setScene(scene);
            stage.setTitle("Handyman Marketplace - " + title);

            FadeTransition ft = new FadeTransition(Duration.millis(300), root);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toggleDarkMode() {
        darkMode = !darkMode;
        if (currentFxml != null) {
            // reload current scene with new theme
            switchScene(currentFxml, stage.getTitle().replace("Handyman Marketplace - ", ""));
        }
    }

    public void showLoginScene() { switchScene("login.fxml", "Login"); }
    public void showRegisterScene() { switchScene("register.fxml", "Register"); }
    public void showCustomerDashboard() { switchScene("customer_dashboard.fxml", "Dashboard"); }
    public void showServicesScene() { switchScene("services.fxml", "Services"); }
    public void showBookingsScene() { switchScene("bookings.fxml", "My Bookings"); }
}
