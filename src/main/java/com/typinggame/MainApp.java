package com.typinggame;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.net.URL;
import com.typinggame.data.Database;
import com.typinggame.api.ApiServer;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        System.out.println("=== Typing Game Boot Sequence ===");

        // Initialize database
        System.out.println("[Init] Starting database...");
        Database.init();
        System.out.println("[Init] Database initialized.");

        // --- NEW: start local API server ---
        try {
            new ApiServer().start(18080);
        } catch (Exception e) {
            System.err.println("Failed to start API: " + e.getMessage());
        }

        //Load the font
        Font.loadFont(getClass().getResourceAsStream("/font/PressStart2P-Regular.ttf"), 12);

        // Load login view FXML
        System.out.println("[FXML] Attempting to load /LoginView.fxml...");
        Parent loginRoot = loadFXML("/LoginView.fxml"); // Use leading slash for classpath root
        if (loginRoot == null) {
            System.err.println("[FXML] ERROR: Could not load LoginView.fxml. Check path and resource folder.");
            return;
        }

        // Configure stage
        primaryStage.setTitle("Typing Game Prototype");
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(960);
        primaryStage.setMinHeight(540);

        // Add team icon
        Image icon = new Image(getClass().getResourceAsStream("/images/TeamIcon.png"));
        primaryStage.getIcons().add(icon);

        // Center content and allow letterboxing
        StackPane container = new StackPane(loginRoot);
        Scene loginScene = new Scene(container, 1280, 720);

        // Bind scale to maintain aspect ratio
        var scale = javafx.beans.binding.Bindings.min(
                loginScene.widthProperty().divide(1920),
                loginScene.heightProperty().divide(1080)
        );
        loginRoot.scaleXProperty().bind(scale);
        loginRoot.scaleYProperty().bind(scale);

        loginScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            }
        });


        primaryStage.setScene(loginScene);
        primaryStage.show();
        System.out.println("[Stage] Login screen displayed.");
    }

    /**
     * Utility method to load FXML files safely.
     * @param path the resource path to the FXML file
     * @return the loaded Parent node, or null if not found
     */
    private Parent loadFXML(String path) {
        try {
            System.out.println("[Loader] Looking for FXML at: " + path);
            URL location = getClass().getResource(path); // Leading slash means absolute classpath
            System.out.println("[Loader] Resolved URL: " + location);

            if (location == null) {
                System.err.println("[Loader] FXML file not found at: " + path);
                return null;
            }

            return FXMLLoader.load(location);
        } catch (Exception e) {
            System.err.println("[Loader] Failed to load FXML: " + path);
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}