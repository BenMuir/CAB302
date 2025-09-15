package com.typinggame.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class MainMenu extends Controller {


    @FXML
    // When the play button is pressed move to the menu for which game mode to play
    public void playButtonPressed(ActionEvent event) throws IOException {
        displayScene("/playmenu.fxml", event);
    }
    // Move to options menu
    public void optionsButtonPressed(ActionEvent event) throws IOException {
        displayScene("/optionsmenu.fxml", event);
    }
    // exit application

    // open the leaderboards
    public void leaderboardButtonPressed(ActionEvent event) throws IOException {
        displayScene("/leaderboards.fxml", event);
    }

    public void profileButtonPressed(ActionEvent event) throws IOException{
        displayScene("/ProfileView.fxml" , event);
    }
//exit app
    public void exitButtonPressed() {
        Platform.exit();
    }
}
