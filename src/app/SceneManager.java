package app;

import controller.ChatController;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
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
            applyScene(root, title);
        } catch (Exception e) {
            System.err.println("Error switching scene: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showChatScene(long bookingId) {
        try {
            currentFxml = "chat.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/chat.fxml"));
            Parent root = loader.load();

            ChatController controller = loader.getController();
            controller.setBookingId(bookingId);

            applyScene(root, "Chat");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyScene(Parent root, String title) {
        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(root);
            stage.setScene(scene);
        } else {
            scene.setRoot(root);
        }

        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

        if (darkMode) {
            if (!root.getStyleClass().contains("dark")) {
                root.getStyleClass().add("dark");
            }
        } else {
            root.getStyleClass().remove("dark");
        }

        stage.setTitle("Handyman Marketplace - " + title);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(400), root);
        scaleIn.setFromX(0.96);
        scaleIn.setFromY(0.96);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        ParallelTransition transition = new ParallelTransition(fadeIn, scaleIn);
        transition.play();
    }

    public void toggleDarkMode() {
        darkMode = !darkMode;
        if (currentFxml != null && !currentFxml.equals("chat.fxml")) {
            String title = stage.getTitle().replace("Handyman Marketplace - ", "");
            switchScene(currentFxml, title);
        } else if (currentFxml != null && currentFxml.equals("chat.fxml")) {
            // For chat, we simply re-apply styles to current root if possible,
            // but simpler to just reload the last known chat if strictly needed.
            // For now, toggle works, but immediate visual update might require reload.
            Scene scene = stage.getScene();
            if(scene != null && scene.getRoot() != null) {
                if(darkMode) scene.getRoot().getStyleClass().add("dark");
                else scene.getRoot().getStyleClass().remove("dark");
            }
        }
    }

    public boolean isDarkMode() { return darkMode; }

    public void showLoginScene() { switchScene("login.fxml", "Login"); }
    public void showRegisterScene() { switchScene("register.fxml", "Register"); }
    public void showCustomerDashboard() { switchScene("customer_dashboard.fxml", "Dashboard"); }
    public void showHandymanDashboard() { switchScene("handyman_dashboard.fxml", "Handyman Dashboard"); }
    public void showServicesScene() { switchScene("services.fxml", "Services"); }
    public void showBookingsScene() { switchScene("bookings.fxml", "My Bookings"); }
}