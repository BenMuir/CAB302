package com.typinggame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Parent loginRoot = loadFXML("/loginView.fxml");
        if (loginRoot == null) {
            System.err.println("ERROR: Could not load loginpage.fxml. Check path and resource folder.");
            return;
        }

        Scene loginScene = new Scene(loginRoot, 800, 500);
        primaryStage.setTitle("Typing Game Prototype");
        primaryStage.setScene(loginScene);
        primaryStage.setResizable(true);
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