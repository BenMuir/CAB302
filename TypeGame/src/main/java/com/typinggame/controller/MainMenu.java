package com.typinggame.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenu {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public ImageView playButton;
    public ImageView optionsButton;
    public ImageView profileButton;
    public ImageView notSureYetButton;

    public void playButtonPressed(MouseEvent mouseEvent) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/playmenu.fxml"));
        stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void optionsButtonPressed(MouseEvent mouseEvent) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/src/main/resources/optionsmenu.fxml"));
        stage = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void randomPressed(MouseEvent mouseEvent) {

    }
}
