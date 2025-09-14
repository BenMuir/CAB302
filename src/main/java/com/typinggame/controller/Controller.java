package com.typinggame.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public abstract class Controller {

    public void displayScene(String fxmlFile, MouseEvent mouseEvent) {
        try {
            URL location = getClass().getResource("/" + fxmlFile);
            if (location == null) {
                System.err.println("ERROR: FXML file not found: /" + fxmlFile);
                return;
            }

            Parent root = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
            Stage stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
            StackPane wrapper = new StackPane(root);
            Scene scene = new Scene(wrapper, 1280,720);
            double designWidth = 1920;
            double designHeight = 1080;

            var scale = javafx.beans.binding.Bindings.min(
                    scene.widthProperty().divide(designWidth),
                    scene.heightProperty().divide(designHeight)
            );

            root.scaleXProperty().bind(scale);
            root.scaleYProperty().bind(scale);

            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();

        } catch (IOException e) {
            System.err.println("Failed to load scene: /" + fxmlFile);
            e.printStackTrace();
        }
    }
}

