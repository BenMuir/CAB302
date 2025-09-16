package com.typinggame.controller;

import com.typinggame.data.Database;
import com.typinggame.data.User;
import com.typinggame.data.UserManager;
import com.typinggame.model.Drill;
import com.typinggame.model.TypingStats;
import com.typinggame.util.SceneManager;
import com.typinggame.util.SentenceProvider;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * TypingGameController handles gameplay logic, UI updates, and user stat tracking.
 * It connects the FXML layout to backend functionality and updates the user's profile after each session.
 * [Ben M – Sept 10 2025]
 */
public class TypingGameController {

    // UI Components
    @FXML private TextFlow displayFlow;
    @FXML private TextField inputField;
    @FXML private Label timerLabel;
    @FXML private Label resultLabel;
    @FXML private Label accuracyLabel;
    @FXML private Label wpmLabel;
    @FXML private Label streakLabel;

    // Optional welcome label (kept if your FXML has it)
    @FXML private Label welcomeLabel;

    // Game State
    private String targetText;
    private long startTime;
    private Timeline timer;
    private TypingStats stats;
    private Drill currentDrill; // which drill is active (optional display/debug)

    // User Context (kept from your code)
    private UserManager userManager;
    private User currentUser;

    /** Injects user context from previous scene */
    public void setUserContext(UserManager manager) {
        this.userManager = manager;
        this.currentUser = manager.getCurrentUser();
    }

    /** Initializes game state and input handling */
    @FXML
    public void initialize() {
        // Pick a sentence (DB-backed SentenceProvider; has safe fallback)
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

    /** Renders sentence with color-coded feedback */
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

        // Apply style once (outside loop)
        displayFlow.setStyle(
                "-fx-font-family: 'Press Start 2P'; " +
                        "-fx-font-size: 24px; " +
                        "-fx-background-color: white; " +
                        "-fx-background-radius: 20; " +
                        "-fx-padding: 20;"
        );
    }

    /** Starts timer and updates UI every second */
    private void startTimer() {
        startTime = System.currentTimeMillis();
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            long elapsed = (System.currentTimeMillis() - startTime) / 1000;
            timerLabel.setText("Time: " + elapsed + "s");
        }));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }

    /** Displays final results and updates user profile (same as your behavior) */
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
            if (userManager != null) userManager.saveCurrentUser();
        }
    }

    /** Resets game state for a new round */
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

        // get a new random sentence (DB-backed provider)
        targetText = SentenceProvider.getSentence();
        currentDrill = null;

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

    // ========= NEW: "Choose Drill" flow =========

    /** Button handler wired from FXML. Shows a dialog of drills from DB. */
    @FXML
    private void chooseDrill() {
        try {
            List<Drill> drills = loadAllDrillsFromDb();
            if (drills.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "No drills found in database.").showAndWait();
                return;
            }

            List<String> titles = new ArrayList<>();
            for (Drill d : drills) titles.add(prettyTitle(d)); // <-- no more "(Tier null)"

            ChoiceDialog<String> dialog = new ChoiceDialog<>(titles.get(0), titles);
            dialog.setTitle("Choose Drill");
            dialog.setHeaderText("Pick a drill to play");
            dialog.setContentText("Drill:");

            var picked = dialog.showAndWait();
            if (picked.isEmpty()) return;

            int idx = titles.indexOf(picked.get());
            Drill chosen = drills.get(idx);
            startRoundWithDrill(chosen);
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load drills:\n" + ex.getMessage()).showAndWait();
        }
    }

    /** Build a friendly label, tolerating your current Drill.java layout. */
    private String prettyTitle(Drill d) {
        // If your Drill still has a String difficultyTier, only use it when non-empty
        String label = null;
        try {
            // Field may not exist; ignore if it doesn't
            var s = d.difficultyTier;
            if (s != null && !s.isBlank()) label = s;
        } catch (Throwable ignored) { }

        if (label == null) {
            int t;
            try { t = d.tier; } catch (Throwable e) { t = -1; }
            label = switch (t) {
                case 1 -> "Easy";
                case 2 -> "Medium";
                case 3 -> "Hard";
                default -> (t > 0 ? "Tier " + t : "Tier ?");
            };
        }
        return d.title + " (" + label + ")";
    }

    // Replace your method with this version
    private List<Drill> loadAllDrillsFromDb() throws Exception {
        List<Drill> out = new ArrayList<>();
        try (Connection c = Database.getConnection()) {
            String bodyCol = findExistingColumn(c, "drills",
                    new String[]{"body", "sentence", "text", "content"});
            String tierCol = null;
            try {
                tierCol = findExistingColumn(c, "drills",
                        new String[]{"tier", "difficulty", "difficulty_tier", "difficultyTier"});
            } catch (Exception ignored) { /* default to 1 */ }

            String sql = "SELECT id, title, " + bodyCol + " AS body, " +
                    (tierCol != null ? tierCol : "1") + " AS tier " +
                    "FROM drills ORDER BY " + (tierCol != null ? tierCol : "1") + ", id";

            try (PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Drill(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("body"),
                            rs.getInt("tier")
                    ));
                }
            }
        }

        // If DB has very few drills, append the baseline set so the user has choice.
        if (out.size() < 3) addFallbackDrills(out);
        return out;
    }

    // Add this helper anywhere in the controller
    private void addFallbackDrills(List<Drill> list) {
        // Use high IDs to avoid clashing with DB rows if you later save them.
        list.add(new Drill(1001, "Easy 1",   "cat dog sun run fun", 1));
        list.add(new Drill(1002, "Easy 2",   "time day night light bright", 1));
        list.add(new Drill(1003, "Easy 3",   "red blue green yellow orange", 1));
        list.add(new Drill(1004, "Medium 1", "The quick brown fox jumps over the lazy dog.", 2));
        list.add(new Drill(1005, "Medium 2", "Typing fast is fun, but accuracy is even better!", 2));
        list.add(new Drill(1006, "Hard 1",   "Complexity arises when we type: symbols, commas, and quotes—yet fluency must remain.", 3));
    }


    /** Return the first existing column from the candidate list. */
    private String findExistingColumn(Connection c, String table, String[] candidates)
            throws Exception {
        java.util.HashSet<String> cols = new java.util.HashSet<>();
        try (var st = c.createStatement();
             var rs = st.executeQuery("PRAGMA table_info('" + table + "')")) {
            while (rs.next()) cols.add(rs.getString("name").toLowerCase());
        }
        for (String cand : candidates) {
            if (cols.contains(cand.toLowerCase())) return cand;
        }
        throw new IllegalStateException(
                "Expected one of " + java.util.Arrays.toString(candidates) +
                        " in table '" + table + "', found " + cols);
    }

    /** Reset UI and start a round with a specific drill. */
    private void startRoundWithDrill(Drill d) {
        this.currentDrill = d;
        this.targetText   = d.body;

        inputField.clear();
        inputField.setEditable(true);
        inputField.setDisable(false);

        resultLabel.setText("");
        accuracyLabel.setText("Accuracy: 0%");
        wpmLabel.setText("WPM: 0");
        timerLabel.setText("Time: 0s");
        streakLabel.setText("Streak: 0");
        displayFlow.getChildren().clear();

        this.stats = new TypingStats(targetText);
        updateDisplay("");
        startTimer();
    }
}
