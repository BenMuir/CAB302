package com.typinggame.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.paint.Color;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.animation.Animation;

import com.typinggame.model.TypingStats;
import com.typinggame.util.SentenceProvider;

/**
 * TypingGameController handles all game logic and UI updates.
 * It connects the FXML layout to backend functionality, tracking user input,
 * updating performance stats, and rendering visual feedback.
 *
 * [Ben M – Aug 16 2025]
 */
public class TypingGameController {

    // UI Components linked via FXML
    @FXML private TextFlow displayFlow;             // Displays the target sentence with colored feedback
    @FXML private TextField inputField;             // User input field
    @FXML private Label timerLabel;                 // Shows elapsed time
    @FXML private Label resultLabel;                // Shows final result summary
    @FXML private Label accuracyLabel;              // Shows live accuracy percentage
    @FXML private Label wpmLabel;                   // Shows live WPM
    @FXML private Label streakLabel;                // Shows current streak count

    // Game State
    private String targetText;                      // The sentence the user must type
    private long startTime;                         // Timestamp when typing begins
    private Timeline timer;                         // JavaFX timer for updating elapsed time
    private TypingStats stats;                      // Tracks typing performance

    /**
     * Called automatically when FXML is loaded.
     * Initializes game state, loads sentence, and sets up input handling.
     */
    @FXML
    public void initialize() {
        targetText = SentenceProvider.getSentence();    // Load a new sentence
        stats = new TypingStats(targetText);            // Initialize stats tracking
        updateDisplay("");                      // Render empty sentence with default styling
        startTimer();                                   // Begin timer updates

        // Handle typing input in real time
        inputField.setOnKeyTyped(e -> {
            String input = inputField.getText();

            updateDisplay(input);                       // Update colored sentence display
            stats.update(input);                        // Store current input
            stats.updateAccuracy(input, targetText);    // Update accuracy stats

            // Calculate elapsed time in minutes for WPM
            long elapsedMillis = System.currentTimeMillis() - startTime;
            double elapsedMinutes = elapsedMillis / 60000.0;

            // Update live WPM
            int liveWPM = stats.calculateWPM(elapsedMinutes);
            wpmLabel.setText("WPM: " + liveWPM);

            // Update live accuracy
            double accuracy = stats.getAccuracy();
            accuracyLabel.setText(String.format("Accuracy: %.2f%%", accuracy));

            // Update streak count (resets on mistake)
            if (!input.isEmpty() && input.length() <= targetText.length()) {
                char inputChar = input.charAt(input.length() - 1);
                char targetChar = targetText.charAt(input.length() - 1);
                stats.updateStreak(inputChar, targetChar);
                streakLabel.setText("Streak: " + stats.getCurrentStreak());
            }

            // Completion check
            if (stats.isComplete()) {
                timer.stop();
                showResults();
                inputField.setEditable(false);
            }
        });
    }

    /**
     * Renders the target sentence with color-coded feedback.
     * Green = correct, Red = incorrect, Black = not yet typed.
     *
     * @param userInput Current input from the user
     *
     * [Ben M – Aug 16 2025]
     */
    private void updateDisplay(String userInput) {
        displayFlow.getChildren().clear();

        for (int i = 0; i < targetText.length(); i++) {
            Text t = new Text(String.valueOf(targetText.charAt(i)));

            if (i < userInput.length()) {
                if (userInput.charAt(i) == targetText.charAt(i)) {
                    t.setFill(Color.GREEN); // Correct character
                } else {
                    t.setFill(Color.RED);   // Incorrect character
                }
            } else {
                t.setFill(Color.BLACK);     // Not yet typed
            }

            displayFlow.getChildren().add(t);
        }
    }

    /**
     * Starts the timer and updates the time label every second.
     * Uses JavaFX Timeline for smooth UI updates.
     *
     * [Ben M – Aug 16 2025]
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
     * Displays final results when typing is complete.
     * Shows time, WPM, accuracy, and best streak.
     *
     * [Ben M – Aug 16 2025]
     */
    private void showResults() {
        long elapsedMillis = System.currentTimeMillis() - startTime;
        double elapsedMinutes = elapsedMillis / 60000.0;

        int wpm = stats.calculateWPM(elapsedMinutes);
        double accuracy = stats.getAccuracy();
        int elapsedSeconds = (int)(elapsedMinutes * 60);
        int bestStreak = stats.getBestStreak();

        resultLabel.setText("Finished! Time: " + elapsedSeconds + "s | WPM: " + wpm +
                " | Accuracy: " + String.format("%.2f%%", accuracy) +
                " | Best Streak: " + bestStreak);
    }

    /**
     * Resets the game state and UI for a new round.
     * Clears input, reloads sentence, and restarts timer.
     *
     * [Ben M – Aug 16 2025]
     */
    @FXML
    private void restartGame() {
        inputField.clear();
        inputField.setEditable(true);
        resultLabel.setText("");
        accuracyLabel.setText("Accuracy: 0%");
        wpmLabel.setText("WPM: 0");
        timerLabel.setText("Time: 0s");
        streakLabel.setText("Streak: 0");
        displayFlow.getChildren().clear();

        targetText = SentenceProvider.getSentence();   // Load new sentence
        stats = new TypingStats(targetText);           // Reset stats
        updateDisplay("");                             // Clear display
        startTimer();                                  // Restart timer
    }
}