package com.typinggame.model;

import com.typinggame.controller.MainMenu;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Main Menu Behaviour.
 * Validates Navigation Logic.
 * [Wren]
 */
public class MainMenuControllerTest {

    @Test
    void testPlayButtonHandler() {
        MainMenu menu = new MainMenu();
        ActionEvent event = new ActionEvent();
        assertDoesNotThrow(() -> menu.playButtonPressed(event));
    }

    @Test
    void testOptionsButtonHandler() {
        MainMenu menu = new MainMenu();
        ActionEvent event = new ActionEvent();
        assertDoesNotThrow(() -> menu.optionsButtonPressed(event));
    }

    @Test
    void testLeaderboardsButtonHandler() {
        MainMenu menu = new MainMenu();
        ActionEvent event = new ActionEvent();
        assertDoesNotThrow(() -> menu.leaderboardButtonPressed(event));
    }

    @Test
    void testProfileButtonHandler() {
        MainMenu menu = new MainMenu();
        ActionEvent event = new ActionEvent();
        assertDoesNotThrow(() -> menu.profileButtonPressed(event));
    }

    @Test
    void testExitButtonHandler() {
        MainMenu menu = new MainMenu();
        ActionEvent event = new ActionEvent();
        assertDoesNotThrow(menu::exitButtonPressed);
    }

}
