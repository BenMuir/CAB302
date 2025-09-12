package com.typinggame.controller;

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
 * It connects the login UI to the backend user system and transitions to the profile view.
 *
 * [Ben M – Sept 10 2025]
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final UserManager userManager = new UserManager(new FileUserRepository());

    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return;
        }

        if (userManager.login(username, password)) {
            System.out.println("Login successful for user: " + username);
            statusLabel.setText("Login successful. Welcome, " + username + "!");
            SceneManager.switchScene(
                    (Stage) usernameField.getScene().getWindow(),
                    "/ProfileView.fxml"
            );
        } else {
            System.out.println("Login failed for user: " + username);
            statusLabel.setText("Login failed. Please check your credentials.");
        }
    }

    @FXML
    public void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return;
        }

        if (userManager.register(username, password)) {
            System.out.println("Registration successful for user: " + username);
            statusLabel.setText("Registration successful. Welcome, " + username + "!");
            SceneManager.switchScene(
                    (Stage) usernameField.getScene().getWindow(),
                    "/ProfileView.fxml"
            );
        } else {
            System.out.println("Registration failed — username already exists: " + username);
            statusLabel.setText("Username already exists.");
        }
    }
}