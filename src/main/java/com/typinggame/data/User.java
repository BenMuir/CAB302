package com.typinggame.data;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a user profile with gameplay statistics and login credentials.
 * Serializable for file-based persistence via FileUserRepository.
 *
 * [Ben M – Sept 10 2025]
 */

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String passwordHash;
    //private int highScore;              // Highest WPM or streak
    //private double bestAccuracy;        // Highest recorded accuracy
    //private int totalSessions;          // Number of completed games

    //private List<Double> sessionAccuracies;
    //private List<Integer> sessionWPMs;

    /**
     * Constructs a new user class with the specified username and hasehd password
     * @param username username of the user
     * @param passwordHash hashed password of the user
     */
    public User(String username, String passwordHash) {
        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("Username cannot be empty");
        if (passwordHash == null || passwordHash.isEmpty())
            throw new IllegalArgumentException("Password hash cannot be empty");

        this.username = username;
        this.passwordHash = passwordHash;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public double getBestAccuracy() {
        try (Connection c = Database.getConnection();
            PreparedStatement ps = c.prepareStatement(
                    "SELECT MAX(accuracy) FROM sessions WHERE user_id = ?")) {
            ps.setInt(1, getUserID());
            ResultSet rs = ps.executeQuery();
            return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("Retrieve accuracy failed");
            return 0;
        }
    }

    public double getBestWPM() {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT MAX(wpm) FROM sessions WHERE user_id = ?")) {
            ps.setInt(1, getUserID());
            ResultSet rs = ps.executeQuery();
            return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("Retrieve WPM failed");
            return 0;
        }
    }

    public int getTotalSessions() {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COUNT(*) FROM sessions WHERE user_id = ?")) {
            ps.setInt(1, getUserID());
            ResultSet rs = ps.executeQuery();
            return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Retrieve accuracy failed");
            return 0;
        }
    }

    public int getUserID() {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id FROM users WHERE username = ?")) {
            ps.setString(1, getUsername());
            ResultSet rs = ps.executeQuery();
            return rs.getInt("id");
        } catch (SQLException e) {
            System.err.println("Retreive ID failed: " + e.getMessage());
            return 0;
        }
    }

    public String getDisplayName() {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "Select display_name FROM user_settings WHERE user_id = ?")) {
            ps.setInt(1, getUserID());
            ResultSet rs = ps.executeQuery();
            return rs.getString("display_name");
        } catch (SQLException e) {
            System.err.println("Retreive ID failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * this updates the users display names in the settings table
     * @param newName new display name to be set to
     */
    public void updateDisplayName(String newName) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE user_settings SET display_name = ? WHERE user_id = ?")) {
            ps.setString(1, newName);
            ps.setInt(2, getUserID());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("updating display name failed");
        }

    }

    public String getFont() {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "Select font_family FROM user_settings WHERE user_id = ?")) {
            ps.setInt(1, getUserID());
            ResultSet rs = ps.executeQuery();
            return rs.getString(1);
        } catch (SQLException e) {
            System.err.println("Retreive ID failed: " + e.getMessage());
            return null;
        }
    }

    public int getFontSize() {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "Select font_size FROM user_settings WHERE user_id = ?")) {
            ps.setInt(1, getUserID());
            ResultSet rs = ps.executeQuery();
            return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Retreive ID failed: " + e.getMessage());
            return 0;
        }
    }

    public String getTheme() {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "Select theme FROM user_settings WHERE user_id = ?")) {
            ps.setInt(1, getUserID());
            ResultSet rs = ps.executeQuery();
            return rs.getString(1);
        } catch (SQLException e) {
            System.err.println("Retreive ID failed: " + e.getMessage());
            return null;
        }
    }

    public String themePath() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("KRILL", "/images/BG-unlocks/default-rank-bg.png");
        map.put("CLOWNFISH", "/images/BG-unlocks/clownfish-rank-bg.png");
        map.put("TUNA", "/images/BG-unlocks/tuna-rank-bg.png");
        map.put("SWORDFISH", "/images/BG-unlocks/swordfish-rank-bg.png");
        map.put("WHALE", "/images/BG-unlocks/whale-rank-bg.png");
        String theme = getTheme();
        return map.get(theme);

    }

    /**
     * Updates all of the user settings with the information handed to the method
     * @param displayName what to change the users display name to
     * @param font what font the user wants to have their stuff changed to
     * @param fontSize the fontsize the user wants
     * @param theme what background theme the user wants
     */
    public void updateAllSettings(String displayName, String font, int fontSize, String theme) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE user_settings SET display_name = ?," +
                             "font_family = ?, font_size = ?," +
                             "theme = ? WHERE user_id = ?")) {
            ps.setString(1, displayName);
            ps.setString(2, font);
            ps.setInt(3, fontSize);
            ps.setString(4, theme);
            ps.setInt(5, getUserID());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Retreive ID failed: " + e.getMessage());
        }
    }

    /**
     * yeah this turns the given user into a string it's kinda not used anymore
     * @return string concatenation of all the user details
     */
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", bestAccuracy=" + getBestAccuracy() +
                ", totalSessions=" + getTotalSessions() +
                '}';
    }
}