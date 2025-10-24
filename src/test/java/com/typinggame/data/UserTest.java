package com.typinggame.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
public class UserTest {

    record UserData(String userName, String password, String displayName, String font, int fontSize, String theme) {}
    protected int userCount = 0;
    static class FakeUserRepo {
        private final Map<Integer, UserData> usermap = new HashMap<>(); //the integer is just like the user id lol
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
            return usermap.get(index).displayName;
        }
        int getFontSize(int index) {
            return usermap.get(index).fontSize;
        }
        String getTheme(int index) {
            return usermap.get(index).theme;
        }
    }
}
