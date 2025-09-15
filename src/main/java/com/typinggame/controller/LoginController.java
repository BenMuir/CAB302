package com.typinggame.controller;

import com.typinggame.data.SqliteUserRepository;
import com.typinggame.data.UserManager;
import com.typinggame.data.FileUserRepository;
import com.typinggame.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * LoginController handles user authentication and registration.
 * It connects the login UI to the backend user system and transitions to the main menu.
 *
 * [Ben M – Sept 13 2025]
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final UserManager userManager = new UserManager(new SqliteUserRepository());

    private static final String MAIN_MENU_FXML = "/MainMenu.fxml";

    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            setStatus("Please enter both username and password.", false);
            return;
        }

        if (userManager.login(username, password)) {
            System.out.println("Login successful for user: " + username);
            setStatus("Login successful. Welcome, " + username + "!", true);
            boolean switched = SceneManager.switchScene(
                    (Stage) usernameField.getScene().getWindow(),
                    MAIN_MENU_FXML
            );
            if (!switched) {
                setStatus("Failed to load main menu. Please try again.", false);
            }
        } else {
            System.out.println("Login failed for user: " + username);
            setStatus("Login failed. Please check your credentials.", false);
        }
    }

    @FXML
    public void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            setStatus("Please enter both username and password.", false);
            return;
        }

        if (userManager.register(username, password)) {
            System.out.println("Registration successful for user: " + username);
            setStatus("Registration successful. Welcome, " + username + "!", true);
            boolean switched = SceneManager.switchScene(
                    (Stage) usernameField.getScene().getWindow(),
                    MAIN_MENU_FXML
            );
            if (!switched) {
                setStatus("Failed to load main menu. Please try again.", false);
            }
        } else {
            System.out.println("Registration failed — username already exists: " + username);
            setStatus("Username already exists.", false);
        }
    }

    /**
     * Updates the status label with styled feedback.
     * @param message the message to display
     * @param success true for green text, false for red
     */
    private void setStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }
}