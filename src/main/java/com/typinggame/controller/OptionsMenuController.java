package com.typinggame.controller;

//import com.typinggame.data.Database;
import com.typinggame.data.Database;
import com.typinggame.data.UserManager;
//import com.typinggame.controller.LoginController;
import com.typinggame.data.User;
//import com.typinggame.data.FileUserRepository;
//import com.typinggame.util.SceneManager;
import com.typinggame.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * OptionsMenuController handles navigation from the options menu
 * back to the main menu.
 *
 * [Ben M – Sept 13 2025]
 */
public class OptionsMenuController {

    @FXML private Button backButton;
    @FXML private Button updateDisplayBtn;
    @FXML private TextField displayNameField;
    private UserManager userManager = LoginController.globalUserManager;
    private User user = userManager.getCurrentUser();

    /**
     * Navigates back to the main menu.
     */
    @FXML
    public void handleBack() {
        SceneManager.switchScene(
                (Stage) backButton.getScene().getWindow(),
                "/MainMenu.fxml"
        );
    }

    @FXML
    public void updateDisplay() {
        int idToUpdate = user.getUserID();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE user_settings SET display_name = ? WHERE user_id = ?")) {
            ps.setString(1, displayNameField.getText().trim());
            ps.setInt(2, idToUpdate);
            ps.executeUpdate();
            updateDisplayBtn.setText("Display name updated!");
        } catch (SQLException e) {
            System.err.println("Retreive ID failed: " + e.getMessage());
        }
    }
}