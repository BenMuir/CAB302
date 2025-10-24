package com.typinggame.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {

    record UserData(String userName, String password, String displayName, String font, int fontSize, String theme) {}
    protected int userCount = 0;
    static class FakeUserRepo {
        int numberOfUsers = 0;
        public final Map<Integer, UserData> usermap = new HashMap<>(); //the integer is just like the user id lol
        String getUserName(int index) {
            return usermap.get(index).userName;
        }
        String getPassword(int index) {
            return usermap.get(index).password;
        }
        String getDisplayName(int index) {
            return usermap.get(index).displayName;
        }
        String getFont(int index) {
            return usermap.get(index).font;
        }
        int getFontSize(int index) {
            return usermap.get(index).fontSize;
        }
        String getTheme(int index) {
            return usermap.get(index).theme;
        }
        void addUser(String userName, String password, String displayName) {
            numberOfUsers++;
            usermap.put(numberOfUsers, new UserData(userName, password, displayName, "default", 8, "default"));
        }
        public void updateSettings(int id, UserData userData) {
            usermap.put(id, userData);
        }
    }
    FakeUserRepo fakeUserRepo = new FakeUserRepo();

    private boolean checkValidEmail(String email) {
        if (!email.contains("@")) {
            return false;
        }
        return true;
    }

    public class FakeUser {
        private String username;
        private String password;
        private UserData userData;
        private int id;
        /**
         * Constructs a new user class with the specified username and hasehd password
         * @param username username of the user
         * @param password hashed password of the user
         */
        public FakeUser(String username, String password) {
            if (username == null || username.isEmpty())
                throw new IllegalArgumentException("Username cannot be empty");
            if (password == null || password.isEmpty())
                throw new IllegalArgumentException("Password cannot be empty");
            if (!checkValidEmail(username)) {
                System.out.println(username);
                throw new IllegalArgumentException("Invalid email");
            }
            this.username = username;
            this.password = password;
            for (int i = 1; i <= fakeUserRepo.usermap.size(); i++) {
                if (fakeUserRepo.usermap.get(i).userName.equals(username)) {
                    this.userData = fakeUserRepo.usermap.get(i);
                    this.id = i;
                    System.out.println(id);
                    break;
                }
            }
        }
        public String getUsername() {
            return fakeUserRepo.getUserName(id);
        }
        public String getPassword() {
            return fakeUserRepo.getPassword(id);
        }
        public int getId() {
            return id;
        }
        public String getDisplayName() {
            return fakeUserRepo.getDisplayName(id);
        }
        public String getFont() {
            return fakeUserRepo.getFont(id);
        }
        public int getFontSize() {
            return fakeUserRepo.getFontSize(id);
        }
        public String getTheme() {
            return fakeUserRepo.getTheme(id);
        }
        public void updateSettings(String displayName, String font, int fontSize, String theme) {
            UserData dataToSend = new UserData(username, password, displayName, font, fontSize, theme);
            fakeUserRepo.updateSettings(id, dataToSend);
        }
    }

    @BeforeEach
    void setup() {
        FakeUserRepo fakeUserRepo = new FakeUserRepo();
    }

    @Test
    public void testDefaultSettings() {
        fakeUserRepo.addUser("testing@gmail.com", "meow", "hello");
        FakeUser testUser = new FakeUser("testing@gmail.com", "meow");
        System.out.println(testUser.getPassword());
        assertEquals("meow", testUser.getPassword());
        assertEquals("default", testUser.getFont());
        assertEquals(8, testUser.getFontSize());
        assertEquals("default", testUser.getTheme());
    }

    @Test
    public void testNotValidEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            new FakeUser("notvalidlol", "meow");
        });
    }

    @Test
    public void updatingUserSettings() {
        fakeUserRepo.addUser("hello@gmail.com", "black templars", "helbrecht");
        FakeUser testUser = new FakeUser("hello@gmail.com", "black templars");
        testUser.updateSettings("grimaldus", "gothic", 5, "grimdark");
        assertEquals("grimaldus", testUser.getDisplayName());
        assertEquals("gothic", testUser.getFont());
        assertEquals(5, testUser.getFontSize());
        assertEquals("grimdark", testUser.getTheme());
    }
}
