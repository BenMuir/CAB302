
package com.typinggame.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
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
            Scene prevScene = stage.getScene();

            double prevWidth = prevScene.getWidth();
            double prevHeight = prevScene.getHeight();

            double designWidth = 1920;
            double designHeight = 1080;


            StackPane wrapper = new StackPane(root);
            Scene scene = new Scene(wrapper, prevWidth, prevHeight);


            var scale = javafx.beans.binding.Bindings.min(
                    scene.widthProperty().divide(designWidth),
                    scene.heightProperty().divide(designHeight)
            );

            root.scaleXProperty().bind(scale);
            root.scaleYProperty().bind(scale);


            stage.setScene(scene);
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
