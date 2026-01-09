package app;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainFX extends Application {

    private static SceneManager sceneManager;

    @Override
    public void start(Stage stage) {
        sceneManager = new SceneManager(stage);
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
