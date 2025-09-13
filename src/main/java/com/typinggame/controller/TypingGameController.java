package com.typinggame.controller;

import com.typinggame.data.User;
import com.typinggame.data.UserManager;
import com.typinggame.model.TypingStats;
import com.typinggame.util.SceneManager;
import com.typinggame.util.SentenceProvider;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * TypingGameController handles gameplay logic, UI updates, and user stat tracking.
 * It connects the FXML layout to backend functionality and updates the user's profile after each session.
 * <p>
 * [Ben M – Sept 10 2025]
 */
public class TypingGameController {

    // UI Components
    @FXML
    private TextFlow displayFlow;
    @FXML
    private TextField inputField;
    @FXML
    private Label timerLabel;
    @FXML
    private Label resultLabel;
    @FXML
    private Label accuracyLabel;
    @FXML
    private Label wpmLabel;
    @FXML
    private Label streakLabel;

    // Game State
    private String targetText;
    private long startTime;
    private Timeline timer;
    private TypingStats stats;

    // User Context
    private UserManager userManager;
    private User currentUser;

    @FXML
    private Label welcomeLabel;

    /**
     * Injects user context from previous scene
     */
    public void setUserContext(UserManager manager) {
        this.userManager = manager;
        this.currentUser = manager.getCurrentUser();
    }

    /**
     * Initializes game state and input handling
     */
    @FXML
    public void initialize() {
        targetText = SentenceProvider.getSentence();
        stats = new TypingStats(targetText);
        updateDisplay("");
        startTimer();

        inputField.setEditable(true);
        inputField.setDisable(false);

        inputField.setOnKeyTyped(e -> {
            String input = inputField.getText();
            updateDisplay(input);
            stats.update(input);
            stats.updateAccuracy(input, targetText);

            long elapsedMillis = System.currentTimeMillis() - startTime;
            double elapsedMinutes = elapsedMillis / 60000.0;

            int liveWPM = stats.calculateWPM(elapsedMinutes);
            double accuracy = stats.getAccuracy();

            wpmLabel.setText("WPM: " + liveWPM);
            accuracyLabel.setText(String.format("Accuracy: %.2f%%", accuracy));

            if (!input.isEmpty() && input.length() <= targetText.length()) {
                char inputChar = input.charAt(input.length() - 1);
                char targetChar = targetText.charAt(input.length() - 1);
                stats.updateStreak(inputChar, targetChar);
                streakLabel.setText("Streak: " + stats.getCurrentStreak());
            }

            if (stats.isComplete()) {
                timer.stop();
                inputField.setEditable(false);
                inputField.setDisable(true);
                showResults();
            }
        });
    }

    /**
     * Renders sentence with color-coded feedback
     */
    private void updateDisplay(String userInput) {
        displayFlow.getChildren().clear();

        for (int i = 0; i < targetText.length(); i++) {
            Text t = new Text(String.valueOf(targetText.charAt(i)));

            if (i < userInput.length()) {
                t.setFill(userInput.charAt(i) == targetText.charAt(i) ? Color.GREEN : Color.RED);
            } else {
                t.setFill(Color.BLACK);
            }

            displayFlow.getChildren().add(t);
        }
    }

    /**
     * Starts timer and updates UI every second
     */
    private void startTimer() {
        startTime = System.currentTimeMillis();
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            long elapsed = (System.currentTimeMillis() - startTime) / 1000;
            timerLabel.setText("Time: " + elapsed + "s");
        }));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }

    /**
     * Displays final results and updates user profile
     */
    private void showResults() {
        long elapsedMillis = System.currentTimeMillis() - startTime;
        double elapsedMinutes = elapsedMillis / 60000.0;

        int wpm = stats.calculateWPM(elapsedMinutes);
        double accuracy = stats.getAccuracy();
        int elapsedSeconds = (int) (elapsedMinutes * 60);
        int bestStreak = stats.getBestStreak();

        resultLabel.setText("Finished! Time: " + elapsedSeconds + "s | WPM: " + wpm +
                " | Accuracy: " + String.format("%.2f%%", accuracy) +
                " | Best Streak: " + bestStreak);

        if (currentUser != null) {
            currentUser.recordSession(accuracy, wpm);
            userManager.saveCurrentUser();
        }
    }

    /**
     * Resets game state for a new round
     */
    @FXML
    private void restartGame() {
        inputField.clear();
        inputField.setEditable(true);
        inputField.setDisable(false);

        resultLabel.setText("");
        accuracyLabel.setText("Accuracy: 0%");
        wpmLabel.setText("WPM: 0");
        timerLabel.setText("Time: 0s");
        streakLabel.setText("Streak: 0");
        displayFlow.getChildren().clear();

        targetText = SentenceProvider.getSentence();
        stats = new TypingStats(targetText);
        updateDisplay("");
        startTimer();
    }

    @FXML
    private void ToProfile() {
        SceneManager.switchScene(
                (Stage) inputField.getScene().getWindow(),
                "/playmenu.fxml"
        );
    }
}