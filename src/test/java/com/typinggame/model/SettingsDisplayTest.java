package com.typinggame.model;
import com.typinggame.config.AppContext;
import com.typinggame.controller.OptionsMenuController;
import com.typinggame.data.SqliteUserRepository;
import com.typinggame.data.User;
import com.typinggame.data.UserManager;
import com.typinggame.config.AppContext;
import com.typinggame.data.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for settings saving and display
 * <p>
 *     [Evan - 19/09/2025]
 */
public class SettingsDisplayTest {
    OptionsMenuController controller =  new OptionsMenuController();
    UserRepository userRepo = new SqliteUserRepository();
    UserManager userManager = new UserManager(userRepo);
    User user = new User("Evan", "");
}
