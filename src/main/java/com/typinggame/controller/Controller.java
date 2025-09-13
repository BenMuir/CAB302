package com.typinggame.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
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

            Parent root = FXMLLoader.load(location);
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load scene: /" + fxmlFile);
            e.printStackTrace();
        }
    }
}