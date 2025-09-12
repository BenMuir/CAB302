package com.typinggame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

/**
 * MainApp launches the Typing Game JavaFX application.
 * It starts with the login screen, then transitions to the main menu.
 *
 * [Ben M - Sept 12 2025]
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load login screen
        URL loginLocation = getClass().getResource("/LoginView.fxml");

        if (loginLocation == null) {
            System.err.println("ERROR: LoginView.fxml not found at /LoginView.fxml");
            return;
        }

        Parent loginRoot = FXMLLoader.load(loginLocation);
        Scene loginScene = new Scene(loginRoot, 400, 250);
        primaryStage.setTitle("Typing Game Prototype");
        primaryStage.setScene(loginScene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // After login, switch to main menu (this would be triggered by LoginController)
        // Example stub — replace with actual navigation logic:
        /*
        Parent mainRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/mainmenu.fxml")));
        StackPane wrapper = new StackPane(mainRoot);
        Scene mainScene = new Scene(wrapper, 1280, 720);

        double designW = 1920;
        double designH = 1080;
        var scale = javafx.beans.binding.Bindings.min(
            mainScene.widthProperty().divide(designW),
            mainScene.heightProperty().divide(designH)
        );

        mainRoot.scaleXProperty().bind(scale);
        mainRoot.scaleYProperty().bind(scale);

        primaryStage.setScene(mainScene);
        primaryStage.setResizable(true);
        */
    }

    public static void main(String[] args) {
        launch(args); // Launch JavaFX application
    }
}