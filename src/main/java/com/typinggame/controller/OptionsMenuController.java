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
}