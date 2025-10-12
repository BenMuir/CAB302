package com.typinggame.service;

import com.typinggame.data.Database;
import com.typinggame.data.DrillRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Dynamically computes how far the user is unlocked, from Level 1 up to whatever exists in the DB.
 *
 * Rule:
 *  • User must complete (≥ 1 session) *all* drills in a level to unlock the next.
 *  • Level 1 is always unlocked by default.
 */
public class ProgressService {

    /**
     * Returns the highest level number the user is allowed to access (inclusive).
     * Example:
     *   If DB has levels 1–10 and the user has fully cleared levels 1–3,
     *   this returns 4 → so Level 4 becomes available.
     */
    public int unlockedUpTo(int userId) {
        DrillRepository repo = new DrillRepository();
        int maxLevelInDb = repo.maxLevel();
        if (maxLevelInDb < 1) return 1;

        int unlocked = 1; // user can always access at least level 1

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
                // ----- total drills in this level -----
                psDrills.setInt(1, level);
                int total = 0;
                try (ResultSet rs = psDrills.executeQuery()) {
                    if (rs.next()) total = rs.getInt(1);
                }

                // If no drills in this level, treat as satisfied and continue
                if (total == 0) continue;

                // ----- drills completed at least once -----
                psDone.setInt(1, userId);
                psDone.setInt(2, level);
                int completed = 0;
                try (ResultSet rs = psDone.executeQuery()) {
                    if (rs.next()) completed = rs.getInt(1);
                }

                // If all drills completed, unlock next level
                if (completed >= total) {
                    unlocked = Math.min(maxLevelInDb, level + 1);
                } else {
                    // Stop once we hit an incomplete level
                    break;
                }
            }

        } catch (Exception e) {
            System.err.println("[ProgressService] unlockedUpTo error: " + e.getMessage());
        }

        return unlocked;
    }
}
