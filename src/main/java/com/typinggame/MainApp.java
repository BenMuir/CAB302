package com.typinggame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.net.URL;
import com.typinggame.data.Database;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        //Database Init
        Database.init();

        Parent loginRoot = loadFXML("/loginView.fxml");
        if (loginRoot == null) {
            System.err.println("ERROR: Could not load loginView.fxml. Check path and resource folder.");
            return;
        }

        primaryStage.setTitle("Typing Game Prototype");
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(960);
        primaryStage.setMinHeight(540);

        // Center content and allow letterboxing
        StackPane container = new StackPane(loginRoot);

        double startHeight = 720;
        double startWidth = 1280;
        Scene loginScene = new Scene(container, startWidth, startHeight);

        // Bind scale to scene size (keep aspect ratio by taking the smaller factor)

        double baseWidth = 1920;
        double baseHeight = 1080;
        var scale = javafx.beans.binding.Bindings.min(
                loginScene.widthProperty().divide(baseWidth),
                loginScene.heightProperty().divide(baseHeight)
        );

        loginRoot.scaleXProperty().bind(scale);
        loginRoot.scaleYProperty().bind(scale);

        primaryStage.setScene(loginScene);
        primaryStage.show();

    }

    /**
     * Utility method to load FXML files safely.
     * @param path the resource path to the FXML file
     * @return the loaded Parent node, or null if not found
     */
    private Parent loadFXML(String path) {
        try {
            URL location = getClass().getResource(path);
            if (location == null) {
                System.err.println("FXML file not found at: " + path);
                return null;
            }
            return FXMLLoader.load(location);
        } catch (Exception e) {
            System.err.println("Failed to load FXML: " + path);
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}