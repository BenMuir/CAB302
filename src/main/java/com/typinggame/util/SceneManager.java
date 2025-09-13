package com.typinggame.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * SceneManager handles switching between FXML views.
 * Centralizes scene transitions for consistency and reuse.
 *
 * [Ben M – Sept 13 2025]
 */
public class SceneManager {

    /**
     * Switches the current stage to a new scene loaded from the given FXML path.
     * @param stage the current stage
     * @param fxmlPath the path to the FXML file (e.g. "/MainMenu.fxml")
     * @return true if successful, false if loading failed
     */
    public static boolean switchScene(Stage stage, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            if (root == null) {
                System.err.println("SceneManager error: FXML not found at " + fxmlPath);
                return false;
            }

            stage.setScene(new Scene(root));
            stage.show();
            return true;

        } catch (IOException e) {
            System.err.println("SceneManager error: Failed to load " + fxmlPath);
            e.printStackTrace();
            return false;
        }
    }

    // TODO: Add fade transitions or scene caching if needed
}