package com.typinggame.controller;

import com.typinggame.config.AppContext;
import com.typinggame.data.SqliteUserRepository;
import com.typinggame.data.UserManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.event.ActionEvent;

/**
 * RegisterController handles navigation and user interface within the sign-up page
 * [Evan 01/10]
 */
public class RegisterController extends Controller {

    @FXML private Button registerButton;
    @FXML private Button backButton;
    @FXML private TextField displayNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorResponse;
    public static final UserManager globalUserManager = new UserManager(new SqliteUserRepository());

    @FXML
    /**
     * Initilaises the error response to be nothing
     */
    public void initialize() {
        errorResponse.setText("");
    }

    @FXML
    /**
     * Handles registering a new user, provides the required feedback if necessary
     * @param event event handed by the fxml action
     */
    public void registerNewUser(ActionEvent event) {
        String displayName = displayNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        if (displayName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorResponse.setText("Please fill out all fields");
            return;
        } else if (!checkValidEmail(email)){
            errorResponse.setText("Please enter a valid email");
        }
        if (globalUserManager.register(email, password)) {
            AppContext.userManager = globalUserManager;
            globalUserManager.getCurrentUser().updateDisplayName(displayName);
            displayScene("/MainMenu.fxml", event);
        } else {
            errorResponse.setText("Email in use");
        }
    }

    @FXML
    /**
     * Sends the user back to the login screen
     * @param event event handed by action
     */
    public void handleBack(ActionEvent event) {
        displayScene("/LoginView.fxml", event);
    }

    /**
     * Checks if the email address is valid
     * @param email string of email address to check
     * @return boolean stating whether it is valid
     */
    private boolean checkValidEmail(String email) {
        if (!email.contains("@")) {
            return false;
        }
        return true;
    }
}
