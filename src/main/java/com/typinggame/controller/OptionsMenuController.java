package com.typinggame.controller;

//import com.typinggame.data.Database;
import com.typinggame.config.AppContext;
import com.typinggame.data.Database;
import com.typinggame.data.UserManager;
//import com.typinggame.controller.LoginController;
import com.typinggame.data.User;
//import com.typinggame.data.FileUserRepository;
//import com.typinggame.util.SceneManager;
import com.typinggame.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
public class OptionsMenuController extends Controller{

    @FXML private Button backButton;
    @FXML private TextField displayNameEntry;
    @FXML private ComboBox<String> fontCombo;
    @FXML private ComboBox<String> fontSizeCombo;
    @FXML private ComboBox<String> themeCombo;
    private UserManager userManager;
    private User user;

    /**
     * Hard coding the options for now. Will make a better solution probably
     */
    @FXML
    public void initialize() {
        this.userManager = AppContext.userManager;
        this.user = userManager.getCurrentUser();
        ObservableList<String> fontOptions = FXCollections.observableArrayList("Pixels", "System", "Fortnite or something idk");
        ObservableList<String> fontSizeOptions = FXCollections.observableArrayList("8", "16", "32");
        ObservableList<String> themeOptions = FXCollections.observableArrayList("Ocean", "Light", "Dark");
        fontCombo.setItems(fontOptions);
        fontSizeCombo.setItems(fontSizeOptions);
        themeCombo.setItems(themeOptions);
        displayNameEntry.setText(user.getDisplayName());
        fontCombo.setValue(user.getFont());
        fontSizeCombo.setValue(String.valueOf(user.getFontSize()));
        themeCombo.setValue(user.getTheme());

    }
    //this only exists for the sake of simplifying making unit tests
    public void updateUserManager(UserManager input){
        this.userManager = input;
    }
    public void updateDisplayEntry(String update) {
        displayNameEntry.setText(update);
    }
    public void updateFont(String update) {
        fontCombo.setValue(update);
    }
    public void updateFontSize(String update) {
        fontSizeCombo.setValue(update);
    }
    public void updateTheme(String update) {
        themeCombo.setValue(update);
    }
    public void updateUser(User update) {
        this.user = update;
    }

    /**
     * Navigates back to the main menu.
     */
    @FXML
    public void handleBack(ActionEvent event) {
        int idToUpdate = user.getUserID();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE user_settings SET display_name = ?," +
                             "font_family = ?, font_size = ?," +
                             "theme = ? WHERE user_id = ?")) {
            ps.setString(1, displayNameEntry.getText().trim());
            ps.setString(2, fontCombo.getSelectionModel().getSelectedItem());
            ps.setInt(3, Integer.parseInt(fontSizeCombo.getSelectionModel().getSelectedItem()));
            ps.setString(4, themeCombo.getSelectionModel().getSelectedItem());
            ps.setInt(5, idToUpdate);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Retreive ID failed: " + e.getMessage());
        }
        displayScene("/mainmenu.fxml", event);
    }

    @FXML
    public void updateDisplay() {
        int idToUpdate = user.getUserID();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE user_settings SET display_name = ? WHERE user_id = ?")) {
            ps.setString(1, displayNameEntry.getText().trim());
            ps.setInt(2, idToUpdate);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Retreive ID failed: " + e.getMessage());
        }
    }
}