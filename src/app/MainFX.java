package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    private static Stage primaryStage;
    private static SceneManager sceneManager;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        sceneManager = new SceneManager(primaryStage);
        sceneManager.showLoginScene();
        primaryStage.setTitle("Handyman Marketplace");
        primaryStage.show();
    }

    public static SceneManager getSceneManager() {
        return sceneManager;
    }

    public static void main(String[] args) {
        // start reminder thread if you want it in FX mode too
        // new BookingReminderThread(new BookingDAO()).start();
        launch(args);
    }
}
