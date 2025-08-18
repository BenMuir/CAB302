package com.typinggame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.util.Objects;

/**
 * MainApp launches the Typing Game JavaFX application.
 * It loads the FXML layout and sets up the primary stage.
 *
 *  [Ben M - Aug 16 2025]
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load FXML layout
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/typinggame.fxml")));

        // Set up scene and stage
        Scene scene = new Scene(root, 400, 250 );  //  Window size
        primaryStage.setTitle("Typing Game Prototype");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // Launch JavaFX application
    }
}

// testing a push to GitHub - Ben Muir

