package com.typinggame.controller;

//import com.typinggame.data.Database;
import com.typinggame.config.AppContext;
import com.typinggame.data.Database;
import com.typinggame.data.UserManager;
//import com.typinggame.controller.LoginController;
import com.typinggame.data.User;
//import com.typinggame.data.FileUserRepository;
//import com.typinggame.util.SceneManager;
import com.typinggame.util.Rank;
import com.typinggame.util.RankLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

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
public class ProfileController extends Controller {

    //@FXML private Label welcomeLabel;
    //@FXML private Label highScoreLabel;
    //@FXML private Label accuracyLabel;
    //@FXML private Label sessionCountLabel;
    @FXML
    private Label displayNameLabel;
    @FXML
    private Label wpmLabel;
    @FXML
    private Label accuracyLabel;
    @FXML
    private Label sessionsLabel;
    @FXML
    private ImageView rankBadgeImageView;

    private UserManager userManager = AppContext.userManager;
    private User user = userManager.getCurrentUser();


    //private final UserManager userManager = new UserManager(new FileUserRepository());
    /**
     * things that need to happen before screen logic starts. mostly changing values in labels
     */
    @FXML
    public void initialize() {
        displayNameLabel.setText(user.getDisplayName());
        wpmLabel.setText(String.valueOf(user.getBestWPM()));
        accuracyLabel.setText(String.valueOf(user.getBestAccuracy()));
        sessionsLabel.setText(String.valueOf(user.getTotalSessions()));
        updateRank();

        //}
    }
    /**
     * handles updating the user settings
     */
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
        //displayNameLabel.setText("");
    }

    public void handleBack(ActionEvent event) {
        displayScene("/MainMenuView.fxml", event);
    }

    public void updateRank() {
        try {
            double highestWPM = user.getBestWPM();
            var rank = Rank.forTypingSpeed(highestWPM);
            rankBadgeImageView.setImage(RankLoader.loadIcon(rank));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
