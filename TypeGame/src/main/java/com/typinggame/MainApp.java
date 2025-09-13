package com.typinggame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
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
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/mainmenu.fxml")));

        // Set up scene and stage
        StackPane wrapper = new StackPane(root);
        Scene scene = new Scene(wrapper, 1280,720);
        double designW = 1920;
        double designH = 1080;

        var scale = javafx.beans.binding.Bindings.min(
                scene.widthProperty().divide(designW),
                scene.heightProperty().divide(designH)
        );

        root.scaleXProperty().bind(scale);
        root.scaleYProperty().bind(scale);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Typing Game Prototype");
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // Launch JavaFX application
    }
}

// testing a push to GitHub - Ben Muir

