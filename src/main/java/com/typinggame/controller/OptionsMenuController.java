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
import java.util.ArrayList;
import java.util.List;

/**
 * OptionsMenuController handles navigation from the options menu
 * back to the main menu.
 *
 * [Ben M – Sept 13 2025]
 */
public class OptionsMenuController extends Controller{

    @FXML private Button updateUserSettingsBtn;
    @FXML private TextField displayNameEntry;
    @FXML private ComboBox<String> fontCombo;
    @FXML private ComboBox<String> fontSizeCombo;
    @FXML private ComboBox<String> themeCombo;
    private UserManager userManager;
    private User user;
    private Rank userRank;


    /**
     * Hard coding the options for now. Will make a better solution probably
     */
    public List<String> lowerRanksHelper(Rank rank) {
        List<String> ranks = new ArrayList<String>();
        if (rank.name().equals("WHALE")) {
            ranks.add("WHALE");
            ranks.add("SWORDFISH");
            ranks.add("TUNA");
            ranks.add("CLOWNFISH");
            ranks.add("KRILL");
        } else if (rank.name().equals("SWORDFISH")) {
            ranks.add("SWORDFISH");
            ranks.add("TUNA");
            ranks.add("CLOWNFISH");
            ranks.add("KRILL");
        } else if (rank.name().equals("TUNA")) {
            ranks.add("TUNA");
            ranks.add("CLOWNFISH");
            ranks.add("KRILL");
        } else if (rank.name().equals("CLOWNFISH")) {
            ranks.add("CLOWNFISH");
            ranks.add("KRILL");
        } else {
            ranks.add("KRILL");
        }
        return ranks;
    }

    @FXML
    public void initialize() {
        this.userManager = AppContext.userManager;
        this.user = userManager.getCurrentUser();
        this.userRank = Rank.forTypingSpeed(user.getBestWPM());
        ObservableList<String> fontOptions = FXCollections.observableArrayList("Pixels", "System", "Fortnite or something idk");
        ObservableList<String> fontSizeOptions = FXCollections.observableArrayList("8", "16", "32");
        //ObservableList<String> themeOptions = FXCollections.observableArrayList("Ocean", "Light", "Dark");
        ObservableList<String> themeOptions = FXCollections.observableArrayList(lowerRanksHelper(userRank));
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
    public void updateUserSettings(ActionEvent event) {
        User userToUpdate = userManager.getCurrentUser();
        String displayName = displayNameEntry.getText().trim();
        String font = fontCombo.getSelectionModel().getSelectedItem();
        int fontSize = Integer.parseInt(fontSizeCombo.getSelectionModel().getSelectedItem());
        String theme = themeCombo.getSelectionModel().getSelectedItem();
        userToUpdate.updateAllSettings(displayName, font, fontSize, theme);
        /**
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
        } */
    }

    @FXML
    public void handleBack(ActionEvent event) {
        displayScene("/mainmenu.fxml", event);
    }
}