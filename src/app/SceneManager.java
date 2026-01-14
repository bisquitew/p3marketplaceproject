package app;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
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
            currentFxml = fxml;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxml));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

            if (darkMode) {
                if (!root.getStyleClass().contains("dark")) {
                    root.getStyleClass().add("dark");
                }
            } else {
                root.getStyleClass().remove("dark");
            }

            stage.setScene(scene);
            stage.setTitle("Handyman Marketplace - " + title);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), root);
            scaleIn.setFromX(0.98);
            scaleIn.setFromY(0.98);
            scaleIn.setToX(1.0);
            scaleIn.setToY(1.0);

            SequentialTransition st = new SequentialTransition();
            st.getChildren().addAll(fadeIn, scaleIn);
            st.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toggleDarkMode() {
        darkMode = !darkMode;
        if (currentFxml != null) {
            String title = stage.getTitle().replace("Handyman Marketplace - ", "");
            switchScene(currentFxml, title);
        }
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void showLoginScene() { switchScene("login.fxml", "Login"); }
    public void showRegisterScene() { switchScene("register.fxml", "Register"); }
    public void showCustomerDashboard() { switchScene("customer_dashboard.fxml", "Dashboard"); }
    public void showHandymanDashboard() { switchScene("handyman_dashboard.fxml", "Handyman Dashboard"); }
    public void showServicesScene() { switchScene("services.fxml", "Services"); }
    public void showBookingsScene() { switchScene("bookings.fxml", "My Bookings"); }
}
