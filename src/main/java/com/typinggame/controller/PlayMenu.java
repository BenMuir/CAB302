package com.typinggame.controller;


import javafx.event.ActionEvent;
import java.io.IOException;

public class PlayMenu extends Controller {

    public void learnModeButtonPressed(ActionEvent event) throws IOException {
        displayScene("/GameView.fxml", event);
    }

    public void raceModeButtonPressed(ActionEvent event) {
    }

    public void backButtonPressed(ActionEvent event) throws IOException {
        displayScene("/mainmenu.fxml", event);
    }
}
