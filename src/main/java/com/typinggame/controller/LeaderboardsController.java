package com.typinggame.controller;

import com.typinggame.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * LeaderboardsController handles navigation from the leaderboard view
 * back to the main menu.
 *
 * [Ben M – Sept 13 2025]
 */
public class LeaderboardsController {

    @FXML private Button backButton;

    /**
     * Navigates back to the main menu.
     */
    @FXML
    public void handleBack() {
        SceneManager.switchScene(
                (Stage) backButton.getScene().getWindow(),
                "/MainMenu.fxml"
        );
    }
}