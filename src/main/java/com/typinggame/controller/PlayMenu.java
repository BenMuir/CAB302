package com.typinggame.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class PlayMenu extends Controller {

    @FXML
    public void onAddCustomDrill(ActionEvent e) {
        // Opens the Custom Drill view
        displayScene("/customdrillview.fxml", e); // make sure filename matches exactly
    }

    @FXML
    public void learnModeButtonPressed(ActionEvent event) {
        // Go to the new Difficulty Select page instead of directly to GameView
        displayScene("/DifficultySelectView.fxml", event);
    }

    @FXML
    public void raceModeButtonPressed(ActionEvent event) {
    }

    @FXML
    public void backButtonPressed(ActionEvent event) {
        // Return to main menu
        displayScene("/mainmenu.fxml", event);
    }
}
