package com.typinggame.data;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user profile with gameplay statistics and login credentials.
 * Serializable for file-based persistence via FileUserRepository.
 *
 * [Ben M – Sept 10 2025]
 */

/**
 * Commented out a bunch of stuff that isn't necessary due to the new db structure. Will go through and
 * completely remove after prototype submission. Just leaving them to be safe
 * -Evan
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

    public User(String username, String passwordHash) {
        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("Username cannot be empty");
        if (passwordHash == null || passwordHash.isEmpty())
            throw new IllegalArgumentException("Password hash cannot be empty");

        this.username = username;
        this.passwordHash = passwordHash;
        //this.highScore = 0;
        //this.bestAccuracy = 0.0;
        //this.totalSessions = 0;
        //this.sessionAccuracies = new ArrayList<>();
        //this.sessionWPMs = new ArrayList<>();
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
/** come back to this later when it's actually needed. just getting what we need working rn
    public int getHighScore() {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement((
                     ""
                     ))
    }
*/
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
//
//    public List<Double> getSessionAccuracies() {
//        return sessionAccuracies;
//    }
//
//    public List<Integer> getSessionWPMs() {
//        return sessionWPMs;
//    }
//
//    // Setters
//    public void setHighScore(int highScore) {
//        this.highScore = highScore;
//    }
//
//    public void setBestAccuracy(double bestAccuracy) {
//        this.bestAccuracy = bestAccuracy;
//    }
//
//    public void incrementSessions() {
//        this.totalSessions++;
//    }

    /**
     * Records a new game session and updates stats accordingly.
     *
     * @param accuracy Accuracy percentage from the session
     * @param wpm Words per minute from the session
     */
    /** Not needed anymore with new databse implementation. Just leaving for now
    public void recordSession(double accuracy, int wpm) {
        sessionAccuracies.add(accuracy);
        sessionWPMs.add(wpm);
        totalSessions++;

        if (accuracy > bestAccuracy) bestAccuracy = accuracy;
        if (wpm > highScore) highScore = wpm;
    }

    /**
     * Resets all gameplay statistics.
     */
//    public void resetStats() {
//        highScore = 0;
//        bestAccuracy = 0.0;
//        totalSessions = 0;
//        sessionAccuracies.clear();
//        sessionWPMs.clear();
//    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", bestAccuracy=" + getBestAccuracy() +
                ", totalSessions=" + getTotalSessions() +
                '}';
    }
}