package com.typinggame.controller;

import com.typinggame.config.AppContext;
import com.typinggame.data.Database;
import com.typinggame.data.DrillRepository;
import com.typinggame.data.SessionRepository;
import com.typinggame.data.SqliteUserRepository;
import com.typinggame.data.User;
import com.typinggame.data.UserManager;
import com.typinggame.model.Drill;
import com.typinggame.model.Session;
import com.typinggame.model.TypingStats;
import com.typinggame.service.ProgressService;
import com.typinggame.util.Rank;
import com.typinggame.util.RankLoader;
import com.typinggame.util.SentenceProvider;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

//keyboard


import javafx.geometry.Insets;



import javafx.scene.input.KeyCode;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import javafx.scene.media.AudioClip;
import java.awt.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import java.time.Instant;

/**
 * TypingGameController handles gameplay logic, UI updates, and user stat tracking.
 * It connects the FXML layout to backend functionality and updates the user's profile after each session.
 * <p>
 * [Ben M – Sept 10 2025]
 */
public class TypingGameController extends Controller {

    // UI Components
    @FXML
    private ImageView bgImage;
    @FXML
    private TextFlow displayFlow;
    @FXML
    private TextField inputField;
    @FXML
    private Label timerLabel;
    @FXML
    private Label accuracyLabel;
    @FXML
    private Label wpmLabel;
    @FXML
    private Label streakLabel;
    @FXML
    private ComboBox<Drill> drillSelect;
    @FXML
    private Button startButton;
    //keyboard
    private final Map<KeyCode, Button> keyMap = new HashMap<>();


    //keyboard row 0

    @FXML
    private Button keyBACKQUOTE;
    @FXML
    private Button key1;
    @FXML
    private Button key2;
    @FXML
    private Button key3;
    @FXML
    private Button key4;
    @FXML
    private Button key5;
    @FXML
    private Button key6;
    @FXML
    private Button key7;
    @FXML
    private Button key8;
    @FXML
    private Button key9;
    @FXML
    private Button key0;
    @FXML
    private Button keyMINUS;
    @FXML
    private Button keyEQUALS;
    @FXML
    private Button keyBACKSPACE;

    //row 1
    @FXML
    private Button keyTAB;
    @FXML
    private Button keyQ;
    @FXML
    private Button keyW;
    @FXML
    private Button keyE;
    @FXML
    private Button keyR;
    @FXML
    private Button keyT;
    @FXML
    private Button keyY;
    @FXML
    private Button keyU;
    @FXML
    private Button keyI;
    @FXML
    private Button keyO;
    @FXML
    private Button keyP;
    @FXML
    private Button keyLBRACKET;
    @FXML
    private Button keyRBRACKET;
    @FXML
    private Button keyBACKSLASH;

    //row 2
    @FXML
    private Button keyCAPS;
    @FXML
    private Button keyA;
    @FXML
    private Button keyS;
    @FXML
    private Button keyD;
    @FXML
    private Button keyF;
    @FXML
    private Button keyG;
    @FXML
    private Button keyH;
    @FXML
    private Button keyJ;
    @FXML
    private Button keyK;
    @FXML
    private Button keyL;
    @FXML
    private Button keySEMICOLON;
    @FXML
    private Button keyQUOTE;
    @FXML
    private Button keyENTER;

    //row 3
    @FXML
    private Button keyLSHIFT;
    @FXML
    private Button keyZ;
    @FXML
    private Button keyX;
    @FXML
    private Button keyC;
    @FXML
    private Button keyV;
    @FXML
    private Button keyB;
    @FXML
    private Button keyN;
    @FXML
    private Button keyM;
    @FXML
    private Button keyCOMMA;
    @FXML
    private Button keyPERIOD;
    @FXML
    private Button keySLASH;
    @FXML
    private Button keyRSHIFT;

    //row 4
    @FXML
    private Button keySPACE;


    // Modifier state
    private boolean[] capsActive = {false};
    private boolean leftShiftActive = false;
    private boolean rightShiftActive = false;
    // WPM
    private final Deque<Integer> wpmHistory = new ArrayDeque<>();
    @FXML
    private LineChart<Number, Number> wpmChart;

    private XYChart.Series<Number, Number> wpmSeries;
    private XYChart.Series<Number, Number> accuracySeries;
    private XYChart.Series<Number, Number> streakSeries;

    // Game State
    private String targetText;
    private long startTime;
    private Timeline timer;
    private TypingStats stats;
    private Drill currentDrill;

    // Data access
    private final SessionRepository sessionRepo = new SessionRepository();
    private final DrillRepository drillRepo = new DrillRepository();

    // User Context
    private UserManager userManager = AppContext.userManager;
    @SuppressWarnings("unused")
    private User currentUser;

    public void setUserContext(UserManager manager) {
        this.userManager = manager;
        this.currentUser = (manager != null) ? manager.getCurrentUser() : null;
    }





    private int resolveUserId() {
        try {
            if (userManager != null && userManager.getCurrentUser() != null)
                return userManager.getCurrentUser().getUserID();
        } catch (Throwable ignore) {
        }

        try {
            if (com.typinggame.config.AppContext.userManager != null &&
                    com.typinggame.config.AppContext.userManager.getCurrentUser() != null)
                return com.typinggame.config.AppContext.userManager.getCurrentUser().getUserID();
        } catch (Throwable ignore) {
        }

        try {
            UserManager um = new UserManager(new SqliteUserRepository());
            if (um.getCurrentUser() != null) return um.getCurrentUser().getUserID();
        } catch (Throwable ignore) {
        }

        return 0;
    }


    // Welcome popup appears on game start
    private void showWelcomePopup(Window owner) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner(owner);
        popup.setTitle("Welcome to Sea Type");

        Label message = new Label("Start typing the sentence on the screen without looking at your keyboard, use the keyboard onscreen to find the keys!");
        Button okButton = new Button("Let's Type");
        okButton.setOnAction(e -> popup.close());

        VBox layout = new VBox(15, message, okButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        popup.setScene(new Scene(layout));
        popup.showAndWait();
    }

    private static final String DEFAULT_KEY_STYLE = "-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: normal;";


    // Define reusable style constants
    private static final String SERVICE_KEY_STYLE = "-fx-text-fill: #ffd700; -fx-font-size: 20px; -fx-font-weight: normal;";
    private static final String SHIFT_STYLE = "-fx-text-fill: #ffd700; -fx-font-size: 20px; -fx-font-weight: normal;";
    private static final String BACKSPACE_STYLE = "-fx-text-fill: #ffd700; -fx-font-size: 20px; -fx-font-weight: normal;";

    // name and rank labels
    public Label nameLabel;
    public Label rankLabel;

    // rank image
    @FXML
    private ImageView rankBadgeImageView;


    // username display
    @FXML
    private Label usernameLabel;
    private boolean usernameApplied = false;


    // sound files
    // 3 correct sounds of bubbles to give variety/depth to user experience. 1 incorrect for recognisable consistency of errors
    private AudioClip[] correctSounds;
    private AudioClip incorrectSound;

    private void initializeSounds() {
        correctSounds = new AudioClip[] {
                new AudioClip(getClass().getResource("/sounds/correct1.mp3").toExternalForm()),
                new AudioClip(getClass().getResource("/sounds/correct2.mp3").toExternalForm()),
                new AudioClip(getClass().getResource("/sounds/correct3.mp3").toExternalForm())
        };
        incorrectSound = new AudioClip(getClass().getResource("/sounds/incorrect.mp3").toExternalForm());
    }

    // begin game state
    private User user;
    @FXML
    private void initialize() {
        user = AppContext.userManager.getCurrentUser(); // fallback
        System.out.println("[Controller] Instance hash: " + System.identityHashCode(this));

        // Load sound effects
        initializeSounds();

        // Initialize chart series before UI
        wpmSeries = new XYChart.Series<>();
        accuracySeries = new XYChart.Series<>();
        streakSeries = new XYChart.Series<>();

        wpmSeries.setName("WPM");
        accuracySeries.setName("Accuracy");
        streakSeries.setName("Streak");

        Platform.runLater(() -> {
            System.out.println("[Controller] TypingGameController initialized");
            setupBG();
            setupChart();
            setupUserDisplay();
            setupInputField();
        });
    }
    //set up chart
    private void setupBG() {
        String resourcePath = user.themePath();
        Image image = new Image(getClass().getResourceAsStream(resourcePath));
        bgImage.setImage(image);
    }
    private void setupChart() {
        if (wpmChart == null) {
            System.out.println("[Controller] wpmChart is null");
            return;
        }

        System.out.println("[Controller] wpmChart injected");

        wpmChart.getData().addAll(wpmSeries, accuracySeries, streakSeries);
        wpmChart.setCreateSymbols(false); // disables dot markers

        NumberAxis xAxis = (NumberAxis) wpmChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) wpmChart.getYAxis();

        xAxis.setLabel("");
        yAxis.setLabel("");

        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        yAxis.setTickLabelsVisible(false);
        yAxis.setTickMarkVisible(false);

        if (wpmChart.getScene() != null) {
            System.out.println("[Controller] wpmChart scene is ready");
            showWelcomePopup(wpmChart.getScene().getWindow());
        } else {
            System.out.println("[Controller] wpmChart scene is null");
        }
    }
    //user display - name,rank,badge
    private void setupUserDisplay() {
        if (user == null) {
            System.out.println("[Controller] No user found in AppContext");
            return;
        }

        String username = user.getUsername();
        double bestWPM = user.getBestWPM();
        Rank rank = Rank.forTypingSpeed(bestWPM);

        System.out.println("[Controller] Retrieved username: " + username);
        System.out.println("[Controller] Best WPM: " + bestWPM);
        System.out.println("[Controller] Resolved rank: " + rank.name());

        if (usernameLabel != null && !usernameApplied) {
            usernameLabel.setText( user.getDisplayName() + "\n\n\n"+ rank.name());
            usernameApplied = true;
            System.out.println("[Controller] Username and rank applied to label");
        } else if (usernameLabel == null) {
            System.out.println("[Controller] usernameLabel is null in setupUserDisplay");
        }

        if (rankBadgeImageView != null) {
            try {
                Image badge = RankLoader.loadIcon(rank);
                rankBadgeImageView.setImage(badge);
                System.out.println("[Controller] Rank badge loaded: " + rank.resourcePath);
            } catch (Exception e) {
                System.err.println("[Controller] Failed to load rank badge: " + e.getMessage());
            }
        } else {
            System.out.println("[Controller] rankBadgeImageView is null");
        }
    }
    // input field - user types here
    private void setupInputField() {
        inputField.setEditable(true);
        inputField.setDisable(false);
        Platform.runLater(() -> inputField.requestFocus());

        inputField.setStyle(
                "-fx-font-family: 'Press Start 2P'; " +
                        "-fx-font-size: 24px; " +
                        "-fx-text-fill: whitesmoke; " +
                        "-fx-background-color: transparent; " +
                        "-fx-background-radius: 14;"
        );

        // Game setup...
        try {
            Database.init();

            int userId = resolveUserId();
            int unlocked = new ProgressService().unlockedUpTo(userId);

            var options = drillRepo.findUpToTier(unlocked);

            if (drillSelect != null) {
                drillSelect.getItems().setAll(options);
                if (!options.isEmpty()) {
                    drillSelect.getSelectionModel().selectFirst();
                    currentDrill = options.get(0);
                    targetText = currentDrill.body;
                    startButton.setDisable(false);
                } else {
                    startButton.setDisable(true);
                    currentDrill = null;
                    targetText = SentenceProvider.getSentence();
                }
            } else {
                currentDrill = options.isEmpty() ? null : options.get(0);
                targetText = (currentDrill != null) ? currentDrill.body : SentenceProvider.getSentence();
            }
        } catch (Exception ex) {
            System.err.println("[GameView] drill init skipped: " + ex.getMessage());
            currentDrill = null;
            targetText = SentenceProvider.getSentence();
        }

        stats = new TypingStats(targetText);
        updateDisplay("");
        startTimer();

        inputField.setEditable(true);
        inputField.setDisable(false);

        inputField.setOnKeyTyped(e -> {
            String rawInput = inputField.getText();
            String input = rawInput == null ? "" : rawInput.trim(); // trim for accuracy calc

            updateDisplay(input);
            stats.update(input);
            stats.updateAccuracy(input, targetText); // now handles edge cases internally

            long elapsedMillis = System.currentTimeMillis() - startTime;
            double elapsedMinutes = elapsedMillis / 60000.0;
            double accuracy = stats.getAccuracy();

            int liveWPM = stats.calculateWPM(elapsedMinutes);
            wpmHistory.addLast(liveWPM);
            if (wpmHistory.size() > 5) wpmHistory.removeFirst();

            int avgWPM = wpmHistory.stream().mapToInt(Integer::intValue).sum() / wpmHistory.size();
            wpmLabel.setText("WPM: " + avgWPM);

            int elapsedSeconds = (int) (elapsedMillis / 1000);
            int currentStreak = stats.getCurrentStreak();

            // Chart updates
            wpmSeries.getData().add(new XYChart.Data<>(elapsedSeconds, avgWPM));
            accuracySeries.getData().add(new XYChart.Data<>(elapsedSeconds, accuracy));
            streakSeries.getData().add(new XYChart.Data<>(elapsedSeconds, currentStreak));

            if (wpmSeries.getData().size() > 50) wpmSeries.getData().remove(0);
            if (accuracySeries.getData().size() > 50) accuracySeries.getData().remove(0);
            if (streakSeries.getData().size() > 50) streakSeries.getData().remove(0);

            accuracyLabel.setText(String.format("Accuracy: %.2f%%", accuracy));
            streakLabel.setText("Streak: " + currentStreak);

            // Sound logic — accurate for all characters including shift-modified
            String typedText = e.getCharacter();
            if (!input.isEmpty()
                    && input.length() <= targetText.length()
                    && typedText != null
                    && typedText.length() == 1
                    && !typedText.equals(" ") // skip space
                    && !typedText.equals("\b")) { // skip backspace

                char typedChar = typedText.charAt(0);
                char expectedChar = targetText.charAt(input.length() - 1);
                boolean isCorrect = typedChar == expectedChar;

                stats.updateStreak(typedChar, expectedChar);

                if (isCorrect) {
                    int soundIndex = (int) (Math.random() * correctSounds.length);
                    correctSounds[soundIndex].play();
                } else {
                    incorrectSound.play();
                }
            }

            // Completion check
            if (stats.isComplete()) {
                try {
                    if (timer != null) timer.stop();
                } catch (Exception ignore) {}

                inputField.setEditable(false);
                inputField.setDisable(true);
                saveSession();
                showResults();
            }
        });

        Platform.runLater(() -> {
            // Key mapping
            keyMap.put(KeyCode.BACK_QUOTE, keyBACKQUOTE);
            keyMap.put(KeyCode.DIGIT1, key1);
            keyMap.put(KeyCode.DIGIT2, key2);
            keyMap.put(KeyCode.DIGIT3, key3);
            keyMap.put(KeyCode.DIGIT4, key4);
            keyMap.put(KeyCode.DIGIT5, key5);
            keyMap.put(KeyCode.DIGIT6, key6);
            keyMap.put(KeyCode.DIGIT7, key7);
            keyMap.put(KeyCode.DIGIT8, key8);
            keyMap.put(KeyCode.DIGIT9, key9);
            keyMap.put(KeyCode.DIGIT0, key0);
            keyMap.put(KeyCode.MINUS, keyMINUS);
            keyMap.put(KeyCode.EQUALS, keyEQUALS);
            keyMap.put(KeyCode.BACK_SPACE, keyBACKSPACE);

            keyMap.put(KeyCode.TAB, keyTAB);
            keyMap.put(KeyCode.Q, keyQ);
            keyMap.put(KeyCode.W, keyW);
            keyMap.put(KeyCode.E, keyE);
            keyMap.put(KeyCode.R, keyR);
            keyMap.put(KeyCode.T, keyT);
            keyMap.put(KeyCode.Y, keyY);
            keyMap.put(KeyCode.U, keyU);
            keyMap.put(KeyCode.I, keyI);
            keyMap.put(KeyCode.O, keyO);
            keyMap.put(KeyCode.P, keyP);
            keyMap.put(KeyCode.OPEN_BRACKET, keyLBRACKET);
            keyMap.put(KeyCode.CLOSE_BRACKET, keyRBRACKET);
            keyMap.put(KeyCode.BACK_SLASH, keyBACKSLASH);

            keyMap.put(KeyCode.CAPS, keyCAPS);
            keyMap.put(KeyCode.A, keyA);
            keyMap.put(KeyCode.S, keyS);
            keyMap.put(KeyCode.D, keyD);
            keyMap.put(KeyCode.F, keyF);
            keyMap.put(KeyCode.G, keyG);
            keyMap.put(KeyCode.H, keyH);
            keyMap.put(KeyCode.J, keyJ);
            keyMap.put(KeyCode.K, keyK);
            keyMap.put(KeyCode.L, keyL);
            keyMap.put(KeyCode.SEMICOLON, keySEMICOLON);
            keyMap.put(KeyCode.QUOTE, keyQUOTE);
            keyMap.put(KeyCode.ENTER, keyENTER);

            keyMap.put(KeyCode.SHIFT, keyLSHIFT); // Left Shift and Right shift light up at same time
            keyMap.put(KeyCode.Z, keyZ);
            keyMap.put(KeyCode.X, keyX);
            keyMap.put(KeyCode.C, keyC);
            keyMap.put(KeyCode.V, keyV);
            keyMap.put(KeyCode.B, keyB);
            keyMap.put(KeyCode.N, keyN);
            keyMap.put(KeyCode.M, keyM);
            keyMap.put(KeyCode.COMMA, keyCOMMA);
            keyMap.put(KeyCode.PERIOD, keyPERIOD);
            keyMap.put(KeyCode.SLASH, keySLASH);


            keyMap.put(KeyCode.SPACE, keySPACE);




            inputField.setOnKeyPressed(event -> {
                KeyCode code = event.getCode();
                String input = inputField.getText();
                int index = input.length();

                System.out.println("Pressed: " + code + " | Text: '" + event.getText() + "'");

                // TAB: prevent focus shift and reclaim focus
                if (code == KeyCode.TAB) {
                    event.consume();
                    keyTAB.setStyle(SERVICE_KEY_STYLE);
                    Platform.runLater(() -> inputField.requestFocus());
                    return;
                }

                // CAPS LOCK: toggle visual state
                if (code == KeyCode.CAPS) {
                    capsActive[0] = !capsActive[0];
                    Platform.runLater(() -> {
                        keyCAPS.setStyle(capsActive[0] ? SERVICE_KEY_STYLE : DEFAULT_KEY_STYLE);
                    });
                    return;
                }

                // SHIFT: light up both sides
                if (code == KeyCode.SHIFT) {
                    event.consume();
                    leftShiftActive = true;
                    rightShiftActive = true;
                    keyLSHIFT.setStyle(SHIFT_STYLE);
                    keyRSHIFT.setStyle(SHIFT_STYLE);
                    return;
                }

                // ENTER: highlight yellow
                if (code == KeyCode.ENTER) {
                    event.consume();
                    keyENTER.setStyle(SERVICE_KEY_STYLE);
                    return;
                }

                // BACKSPACE: highlight yellow
                if (code == KeyCode.BACK_SPACE) {
                    event.consume();
                    keyBACKSPACE.setStyle(BACKSPACE_STYLE);
                    return;
                }

                // feedback (green/red)
                if (!event.getText().isEmpty()) {
                    String typedChar = event.getText();
                    String expectedChar = index < targetText.length() ? String.valueOf(targetText.charAt(index)) : "";
                    boolean isCorrect = typedChar.equals(expectedChar);
                    highlightKey(code, isCorrect);
                }
            });

            inputField.setOnKeyReleased(event -> {
                KeyCode code = event.getCode();

                // TAB release
                if (code == KeyCode.TAB) {
                    event.consume();
                    keyTAB.setStyle(DEFAULT_KEY_STYLE);
                    return;
                }

                // SHIFT release — clear both sides
                if (code == KeyCode.SHIFT) {
                    event.consume();
                    if (leftShiftActive || rightShiftActive) {
                        keyLSHIFT.setStyle(DEFAULT_KEY_STYLE);
                        keyRSHIFT.setStyle(DEFAULT_KEY_STYLE);
                        leftShiftActive = false;
                        rightShiftActive = false;
                    }
                    unhighlightKey(code);
                    return;
                }

                // ENTER - clear colour
                if (code == KeyCode.ENTER) {
                    event.consume();
                    keyENTER.setStyle(DEFAULT_KEY_STYLE);
                    return;
                }

                // BACKSPACE - clear colour
                if (code == KeyCode.BACK_SPACE) {
                    event.consume();
                    keyBACKSPACE.setStyle(DEFAULT_KEY_STYLE);
                    return;
                }

                unhighlightKey(code);
            });
        });
        ;
    }

    // Keyboard
    private void highlightKey(KeyCode code, boolean isCorrect) {
        Button key = keyMap.get(code);
        if (key != null) {
            // Skip styling Caps entirely — let toggle handle it
            if (code == KeyCode.CAPS) return;
            // Wren - Testing the text fill with the newly styled keycaps
            if (isCorrect) {
                key.setStyle("-fx-text-fill: #00ff00;"); //-fx-text-fill: black;"); - old line
            } else {
                key.setStyle("-fx-text-fill: #ff4444;"); // -fx-text-fill: white;"); - old line
            }
        }
    }
    private void unhighlightKey(KeyCode code) {
        Button key = keyMap.get(code);
        if (key != null) {
            // Don't reset CAPS if it's still active
            if (code == KeyCode.CAPS && capsActive[0]) return;

            key.setStyle(DEFAULT_KEY_STYLE); // Always restore baseline
        }
    }


    /**
     * starts selected drill
     */
    @FXML
    private void startSelectedDrill(ActionEvent e) {
        Drill choice = null;
        if (drillSelect != null) {
            choice = drillSelect.getSelectionModel().getSelectedItem();
            if (choice == null && !drillSelect.getItems().isEmpty()) {
                choice = drillSelect.getItems().get(0);
                drillSelect.getSelectionModel().selectFirst();
            }
        }

        if (choice != null) {
            loadDrill(choice);
        } else {
            loadRandomDrill();
        }

        // Reset chart lines
        wpmSeries.getData().clear();
        accuracySeries.getData().clear();
        streakSeries.getData().clear();

        wpmChart.getData().clear();
        wpmChart.getData().addAll(wpmSeries, accuracySeries, streakSeries);

        // Reset and focus input field -Ben
        inputField.clear();
        inputField.setEditable(true);
        inputField.setDisable(false);
        Platform.runLater(() -> inputField.requestFocus());
    }

    /**
     * Resets game state for a new round
     */
    @FXML
    private void restartGame() {
        try {
            if (timer != null) timer.stop();
        } catch (Exception ignore) {}

        // Reset UI stats
        accuracyLabel.setText("Accuracy: 0%");
        wpmLabel.setText("WPM: 0");
        timerLabel.setText("Time: 0s");
        streakLabel.setText("Streak: 0");
        displayFlow.getChildren().clear();

        // Reset input field
        inputField.clear();
        inputField.setEditable(true);
        inputField.setDisable(false);
        Platform.runLater(() -> inputField.requestFocus());

        // Reset chart lines and force refresh
        wpmSeries.getData().clear();
        accuracySeries.getData().clear();
        streakSeries.getData().clear();

        wpmChart.getData().clear(); // remove all series
        wpmChart.getData().addAll(wpmSeries, accuracySeries, streakSeries); // re-add empty series

        // Restart the current drill
        Drill d = (currentDrill != null)
                ? currentDrill
                : (drillSelect != null ? drillSelect.getSelectionModel().getSelectedItem() : null);

        if (d == null && drillSelect != null && !drillSelect.getItems().isEmpty()) {
            d = drillSelect.getItems().get(0);
            drillSelect.getSelectionModel().selectFirst();
        }

        if (d != null) loadDrill(d);
        else loadRandomDrill();
    }
    @FXML
    private void ToProfile(ActionEvent event) {
        displayScene("/playmenu.fxml", event);
    }


    /**
     * Load a specific drill and (re)start the game.
     */
    private void loadDrill(Drill d) {
        currentDrill = d;
        try {
            if (timer != null) timer.stop();
        } catch (Exception ignore) {
        }

        inputField.clear();
        inputField.setEditable(true);
        inputField.setDisable(false);

        targetText = (d != null) ? d.body : SentenceProvider.getSentence();
        stats = new TypingStats(targetText);
        updateDisplay("");
        startTimer();
    }

    /**
     * Load a random sentence as a dummy drill (fallback).
     */
    private void loadRandomDrill() {
        Drill random = new Drill(0, "Random", SentenceProvider.getSentence(), 1);
        loadDrill(random);
    }

    /**
     * Persist the finished session, then refresh unlocked drills.
     */
    private void saveSession() {
        try {
            int userId = resolveUserId();
            System.out.println("[GameView] resolveUserId() = " + userId);

            double accuracyPct = stats.getAccuracy(); // 0..100
            long elapsedMillis = System.currentTimeMillis() - startTime;
            double elapsedMinutes = elapsedMillis / 60000.0;
            int wpm = stats.calculateWPM(elapsedMinutes);
            int typed = inputField.getText().length();
            double durationSeconds = elapsedMillis / 1000.0;
            int drillId = (currentDrill != null) ? currentDrill.id : 1;

            Session s = new Session(
                    null,               // id (auto)
                    userId,
                    drillId,
                    wpm,
                    accuracyPct,
                    typed,
                    durationSeconds,
                    Instant.now()
            );

            sessionRepo.insert(s);
            System.out.println("[GameView] Session saved: user=" + userId +
                    " drill=" + drillId + " wpm=" + wpm + " acc=" + accuracyPct);

            // Refresh dropdown if a new tier unlocked
            int unlocked = new ProgressService().unlockedUpTo(userId);
            var options = drillRepo.findUpToTier(unlocked);
            if (drillSelect != null) {
                drillSelect.getItems().setAll(options);
                // keep current drill selected if still present; otherwise select first
                if (currentDrill != null && options.contains(currentDrill)) {
                    drillSelect.getSelectionModel().select(currentDrill);
                } else if (!options.isEmpty()) {
                    drillSelect.getSelectionModel().selectFirst();
                }
            }
        } catch (Exception saveEx) {
            System.err.println("[GameView] Failed to save session: " + saveEx.getMessage());
            saveEx.printStackTrace();
        }
    }

    /**
     * Renders sentence with color-coded feedback (matches your original look).
     */
    private void updateDisplay(String userInput) {
        displayFlow.getChildren().clear();

        for (int i = 0; i < targetText.length(); i++) {
            Text t = new Text(String.valueOf(targetText.charAt(i)));
            if (i < userInput.length()) {
                t.setFill(userInput.charAt(i) == targetText.charAt(i) ? Color.GREEN : Color.RED);
            } else {
                t.setFill(Color.WHITESMOKE);
            }
            displayFlow.getChildren().add(t);
        }

        // style once (not inside the loop)
        displayFlow.setStyle(
                "-fx-font-family: 'Press Start 2P'; " +
                        "-fx-font-size: 24px; " +
                        "-fx-background-color: transparent; " +
                        "-fx-background-radius: 20; " +
                        "-fx-padding: 20;"
        );
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
        double elapsedMin = elapsedMillis / 60000.0;

        int wpm = stats.calculateWPM(elapsedMin);
        double accuracy = stats.getAccuracy();
        int bestStreak = stats.getBestStreak();

        // Bottom labels already show live info; no top-left result label used.
        wpmLabel.setText("WPM: " + wpm);
        accuracyLabel.setText(String.format("Accuracy: %.2f%%", accuracy));
        streakLabel.setText("Streak: " + bestStreak);
    }
}
