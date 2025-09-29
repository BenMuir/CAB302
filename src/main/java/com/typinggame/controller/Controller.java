
package com.typinggame.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class Controller {
    public boolean displayScene(String fxmlFile, ActionEvent event) {
        try {
            URL location = getClass().getResource(fxmlFile);
            if (location == null) {
                System.err.println("ERROR: FXML file not found: " + fxmlFile);
                return false;
            }

            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            Scene currentScene = stage.getScene();

            double designWidth = 1920;
            double designHeight = 1080;

            StackPane wrapper = new StackPane(root);

            // Get the existing wrapper from current scene if it exists
            if (currentScene.getRoot() instanceof StackPane) {
                StackPane existingWrapper = (StackPane) currentScene.getRoot();

                // Just replace the content instead of creating new scene
                existingWrapper.getChildren().clear();
                existingWrapper.getChildren().add(root);

                // Rebind scale for new root
                var scale = javafx.beans.binding.Bindings.min(
                        currentScene.widthProperty().divide(designWidth),
                        currentScene.heightProperty().divide(designHeight)
                );

                root.scaleXProperty().bind(scale);
                root.scaleYProperty().bind(scale);

            } else {
                // First time or different structure - create new scene
                Scene scene = new Scene(wrapper, currentScene.getWidth(), currentScene.getHeight());

                var scale = javafx.beans.binding.Bindings.min(
                        scene.widthProperty().divide(designWidth),
                        scene.heightProperty().divide(designHeight)
                );

                root.scaleXProperty().bind(scale);
                root.scaleYProperty().bind(scale);

                scene.setOnKeyPressed(fullscreenPressed -> {
                    if (fullscreenPressed.getCode() == KeyCode.F11) {
                        stage.setFullScreen(!stage.isFullScreen());
                    }
                });

                stage.setScene(scene);
            }

            stage.setResizable(true);
            stage.show();
            return true;

        } catch (IOException e) {
            System.err.println("Failed to load scene: /" + fxmlFile);
            e.printStackTrace();
            return false;
        }
    }
}