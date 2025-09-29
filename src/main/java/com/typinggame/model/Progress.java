package com.typinggame.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Tracks a user's overall progress in the typing game.
 * Includes unlocked drills, stats, and rank.
 */
public class Progress implements Serializable {

    // The user this progress belongs to
    public final int userId;

    // Set of drill IDs that the user has unlocked
    public final Set<Integer> unlockedDrillIds = new HashSet<>();

    // Total number of sessions (games) played by this user
    public int totalSessions = 0;

    // Highest words-per-minute achieved across all sessions
    public double bestWpm = 0.0;

    // Highest accuracy percentage achieved across all sessions
    public double bestAccuracy = 0.0;

    // Best score achieved (WPM × Accuracy)
    public double bestScore = 0.0;

    // User's current rank (simple placeholder, e.g. 0..5)
    public int rank = 0;

    /**
     * Creates a new progress tracker for a given user.
     *
     * @param userId the ID of the user this progress belongs to
     */
    public Progress(int userId){
        this.userId = userId;
    }
}
