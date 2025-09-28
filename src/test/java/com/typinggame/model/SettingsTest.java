package com.typinggame.model;
import com.typinggame.controller.MainMenu;
import com.typinggame.controller.OptionsMenuController;
import com.typinggame.data.SqliteUserRepository;
import com.typinggame.data.User;
import com.typinggame.data.UserManager;
import com.typinggame.data.UserRepository;
import com.typinggame.util.SceneManager;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SettingsTest {
    UserRepository userRepository = new SqliteUserRepository();
    UserManager tempUserManager = new UserManager(userRepository);
    User user;
    @BeforeEach
    public void initialise() throws IOException {
        if (!userRepository.userExists("Unique")) {
            tempUserManager.register("Unique", "goober");
        } else {
            tempUserManager.login("Unique", "goober");
        }
        this.user = tempUserManager.getCurrentUser();
    }

    @Test
    /**
     * Test the defaults
     */
    public void testDefaultSettings() {
        assertEquals("System", user.getFont());
        assertEquals(16, user.getFontSize());
        assertEquals("Light", user.getTheme());
    }

    @Test
    /**
     * Test all the things that can be changed
     * [Evan 19/09]
     */
    public void testDisplayNameChange() {
        OptionsMenuController controller = new OptionsMenuController();
        controller.updateUserManager(tempUserManager);
        controller.updateDisplayEntry("High Marshal");
        controller.handleBack(null);
        assertEquals("High Marshal", user.getDisplayName());
    }


    @Test
    public void testFontChange() {
        OptionsMenuController controller = new OptionsMenuController();
        controller.updateUserManager(tempUserManager);
        controller.updateFont("Pixels");
        controller.handleBack(null);
        assertEquals("Pixels", user.getFont());
    }

    @Test
    public void testFontSizeChange() {
        OptionsMenuController controller = new OptionsMenuController();
        controller.updateUserManager(tempUserManager);
        controller.updateFontSize("32");
        controller.handleBack(null);
        assertEquals("32", Integer.toString(user.getFontSize()));
    }
}
