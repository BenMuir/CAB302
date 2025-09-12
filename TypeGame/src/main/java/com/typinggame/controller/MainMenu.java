package com.typinggame.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenu extends Controller {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public ImageView playButton;
    public ImageView optionsButton;
    public ImageView profileButton;
    public ImageView exitButton;
    public ImageView leaderboardButton;

    // When the play button is pressed move to the menu for which game mode to play
    public void playButtonPressed(MouseEvent mouseEvent) throws IOException {
        displayScene("playmenu.fxml", mouseEvent);
    }
    // Move to options menu
    public void optionsButtonPressed(MouseEvent mouseEvent) throws IOException {
        displayScene("optionsmenu.fxml", mouseEvent);
    }
    // exit application

    // open the leaderboards
    public void leaderboardButtonPressed(MouseEvent mouseEvent) throws IOException {
        displayScene("leaderboards.fxml", mouseEvent);
    }

    public void exitPressed(MouseEvent mouseEvent) {
        Platform.exit();
    }

    public void profileButtonPressed(MouseEvent mouseEvent) throws IOException{
        displayScene("profile.fxml" , mouseEvent);
    }
}
