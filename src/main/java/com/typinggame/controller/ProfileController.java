package com.typinggame.controller;

import com.typinggame.data.UserManager;
import com.typinggame.data.User;
import com.typinggame.data.FileUserRepository;
import com.typinggame.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * ProfileController displays user statistics and handles navigation
 * to gameplay, logout, and main menu.
 *
 * [Ben M – Sept 13 2025]
 */
public class ProfileController {

    @FXML private Label welcomeLabel;
    @FXML private Label highScoreLabel;
    @FXML private Label accuracyLabel;
    @FXML private Label sessionCountLabel;

    private final UserManager userManager = new UserManager(new FileUserRepository());

    @FXML
    public void initialize() {
        User user = userManager.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getUsername() + "!");
            highScoreLabel.setText("High Score: " + user.getHighScore());
            accuracyLabel.setText("Best Accuracy: " + String.format("%.2f%%", user.getBestAccuracy()));
            sessionCountLabel.setText("Sessions Played: " + user.getTotalSessions());
        }
    }

    @FXML
    public void handleStartGame() {
        SceneManager.switchScene(
                (Stage) welcomeLabel.getScene().getWindow(),
                "/GameView.fxml"
        );
    }

    @FXML
    public void handleLogout() {
        userManager.logout();
        SceneManager.switchScene(
                (Stage) welcomeLabel.getScene().getWindow(),
                "/LoginView.fxml"
        );
    }

    /**
     * Navigates back to the main menu.
     */
    @FXML
    public void handleBack() {
        SceneManager.switchScene(
                (Stage) welcomeLabel.getScene().getWindow(),
                "/mainmenu.fxml"
        );
    }
}
