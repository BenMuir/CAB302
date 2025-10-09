package com.typinggame.service;

import com.typinggame.data.Database;
import com.typinggame.data.DrillRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Dynamically computes how far the user is unlocked, from Level 1 up to whatever exists in DB.
 * Rule: user must complete (≥1 session) ALL drills in each level to unlock the next level.
 */
public class ProgressService {

    /**
     * Returns the highest level number the user is allowed to access (inclusive).
     * Example: if DB has levels up to 10 and the user fully cleared levels 1..3,
     * this returns 4 (so level 4 becomes available).
     */
    public int unlockedUpTo(int userId) {
        int maxLevelInDb = new DrillRepository().maxLevel();
        if (maxLevelInDb < 1) return 1;

        int unlocked = 1;  // by default the user can access level 1

        final String countDrillsSql =
                "SELECT COUNT(*) FROM drills WHERE level = ?";
        final String countCompletedSql =
                "SELECT COUNT(DISTINCT s.drill_id) " +
                        "FROM sessions s " +
                        "JOIN drills d ON d.id = s.drill_id " +
                        "WHERE s.user_id = ? AND d.level = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement psDrills = c.prepareStatement(countDrillsSql);
             PreparedStatement psDone   = c.prepareStatement(countCompletedSql)) {

            for (int level = 1; level <= maxLevelInDb; level++) {
                // total drills in this level
                psDrills.setInt(1, level);
                int total;
                try (ResultSet rs = psDrills.executeQuery()) {
                    total = rs.next() ? rs.getInt(1) : 0;
                }

                // if no drills in this level, treat as satisfied and continue
                if (total == 0) continue;

                // how many of this level's drills has the user completed at least once?
                psDone.setInt(1, userId);
                psDone.setInt(2, level);
                int completed;
                try (ResultSet rs = psDone.executeQuery()) {
                    completed = rs.next() ? rs.getInt(1) : 0;
                }

                if (completed >= total) {
                    unlocked = Math.min(maxLevelInDb, level + 1);
                } else {
                    break; // stop at the first level not fully completed
                }
            }
        } catch (Exception e) {
            System.err.println("[Progress] unlockedUpTo error: " + e.getMessage());
        }
        return unlocked;
    }
}
