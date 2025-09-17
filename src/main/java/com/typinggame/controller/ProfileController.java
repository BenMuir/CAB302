package com.typinggame.controller;

//import com.typinggame.data.Database;
import com.typinggame.data.Database;
import com.typinggame.data.UserManager;
//import com.typinggame.controller.LoginController;
import com.typinggame.data.User;
//import com.typinggame.data.FileUserRepository;
//import com.typinggame.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ProfileController displays user statistics and handles navigation
 * to gameplay, logout, and main menu.
 *
 * [Ben M – Sept 13 2025]
 */
public class ProfileController {

    //@FXML private Label welcomeLabel;
    //@FXML private Label highScoreLabel;
    //@FXML private Label accuracyLabel;
    //@FXML private Label sessionCountLabel;
    @FXML
    private Label displayNameLabel;
    private UserManager userManager = LoginController.globalUserManager;
    private User user = userManager.getCurrentUser();

    //private final UserManager userManager = new UserManager(new FileUserRepository());

    @FXML
    public void initialize() {
        if (user != null) {
            //welcomeLabel.setText("Welcome, " + user.getUsername() + "!");
            //highScoreLabel.setText("High Score: " + user.getHighScore());
            //accuracyLabel.setText("Best Accuracy: " + String.format("%.2f%%", user.getBestAccuracy()));
            //sessionCountLabel.setText("Sessions Played: " + user.getTotalSessions());
            displayNameLabel.setText("FUCK YEAH");
        }
    }

    @FXML
    public void updateDisplay() {
        int idToSearch = user.getUserID();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "Select display_name FROM user_settings WHERE user_id = ?")) {
            ps.setInt(1, idToSearch);
            ResultSet rs = ps.executeQuery();
            displayNameLabel.setText("Display Name: " + rs.getString("display_name"));
        } catch (SQLException e) {
            System.err.println("Retreive ID failed: " + e.getMessage());
        }
        //displayNameLabel.setText("Fuck me lol");
    }
}
/**
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
 */
/**
 * Navigates back to the main menu.
 */
/**
 @FXML
 public void handleBack() {
 SceneManager.switchScene(
 (Stage) welcomeLabel.getScene().getWindow(),
 "/mainmenu.fxml"
 );
 }
 } */