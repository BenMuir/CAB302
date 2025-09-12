package com.typinggame.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    private int highScore;              // Highest WPM or streak
    private double bestAccuracy;        // Highest recorded accuracy
    private int totalSessions;          // Number of completed games

    private List<Double> sessionAccuracies;
    private List<Integer> sessionWPMs;

    public User(String username, String passwordHash) {
        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("Username cannot be empty");
        if (passwordHash == null || passwordHash.isEmpty())
            throw new IllegalArgumentException("Password hash cannot be empty");

        this.username = username;
        this.passwordHash = passwordHash;
        this.highScore = 0;
        this.bestAccuracy = 0.0;
        this.totalSessions = 0;
        this.sessionAccuracies = new ArrayList<>();
        this.sessionWPMs = new ArrayList<>();
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public int getHighScore() {
        return highScore;
    }

    public double getBestAccuracy() {
        return bestAccuracy;
    }

    public int getTotalSessions() {
        return totalSessions;
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
                ", highScore=" + highScore +
                ", bestAccuracy=" + bestAccuracy +
                ", totalSessions=" + totalSessions +
                '}';
    }
}