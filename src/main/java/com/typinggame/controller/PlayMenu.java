package com.typinggame.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class PlayMenu extends Controller {

    @FXML
    private void onAddCustomDrill(ActionEvent e) {
        // Use the base Controller helper, like your other handlers do
        displayScene("/customdrillview.fxml", e); // make sure filename matches exactly
    }

    // existing handlers (examples)
    public void learnModeButtonPressed(ActionEvent event) {
        displayScene("/GameView.fxml", event);
    }

    public void raceModeButtonPressed(ActionEvent event) {
    }

    public void backButtonPressed(ActionEvent event) {
        displayScene("/mainmenu.fxml", event); // fix any typos in filename
    }
}
