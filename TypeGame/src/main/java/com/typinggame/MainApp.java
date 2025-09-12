package com.typinggame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * MainApp launches the Typing Game JavaFX application.
 * It loads the login screen and sets up the primary stage.
 *
 * [Ben M - Sept 10 2025]
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL location = getClass().getResource("/LoginView.fxml");

        if (location == null) {
            System.err.println("ERROR: LoginView.fxml not found at /LoginView.fxml");
            return;
        }

        Parent root = FXMLLoader.load(location);

        // Set up scene and stage
        Scene scene = new Scene(root, 400, 250);  // Window size
        primaryStage.setTitle("Typing Game Prototype");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // Launch JavaFX application
    }
}