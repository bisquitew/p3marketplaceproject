package app;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainFX extends Application {

    private static SceneManager sceneManager;

    @Override
    public void start(Stage stage) {
        sceneManager = new SceneManager(stage);

        // Professional window settings
        stage.setWidth(1280);
        stage.setHeight(800);
        stage.setMinWidth(1024);
        stage.setMinHeight(768);

        // Set Fullscreen but allow exit via ESC
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("ESC to minimize | Professional Marketplace");

        sceneManager.showLoginScene();
        stage.show();
    }

    public static SceneManager getSceneManager() {
        return sceneManager;
    }

    public static void main(String[] args) {
        launch(args);
    }
}