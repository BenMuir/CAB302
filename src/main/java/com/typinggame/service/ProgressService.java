package com.typinggame.service;

import com.typinggame.data.Database;
import com.typinggame.data.DrillRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Dynamically computes how far the user is unlocked, from Tier 1 up to whatever exists in DB.
 * Rule: must complete (≥1 session) ALL drills in each tier to unlock the next tier.
 */
public class ProgressService {

    /**
     * Returns the highest tier number the user is allowed to access (inclusive).
     * If DB has tiers up to 5 and user cleared tiers 1..3 fully, this returns 4 (so tier 4 shows up).
     */
    public int unlockedUpTo(int userId) {
        int maxTierInDb = new DrillRepository().maxTier();
        if (maxTierInDb < 1) return 1;

        int unlocked = 1;  // by default the user can see tier 1

        final String countDrillsSql =
                "SELECT COUNT(*) FROM drills WHERE tier = ?";
        final String countCompletedSql =
                "SELECT COUNT(DISTINCT s.drill_id) " +
                        "FROM sessions s " +
                        "JOIN drills d ON d.id = s.drill_id " +
                        "WHERE s.user_id = ? AND d.tier = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement psDrills = c.prepareStatement(countDrillsSql);
             PreparedStatement psDone   = c.prepareStatement(countCompletedSql)) {

            for (int tier = 1; tier <= maxTierInDb; tier++) {
                // total drills in this tier
                psDrills.setInt(1, tier);
                int total;
                try (ResultSet rs = psDrills.executeQuery()) {
                    total = rs.next() ? rs.getInt(1) : 0;
                }

                // if no drills in this tier, skip as satisfied
                if (total == 0) continue;

                // completed at least once?
                psDone.setInt(1, userId);
                psDone.setInt(2, tier);
                int completed;
                try (ResultSet rs = psDone.executeQuery()) {
                    completed = rs.next() ? rs.getInt(1) : 0;
                }

                if (completed >= total) {
                    unlocked = Math.min(maxTierInDb, tier + 1);
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("[Progress] unlockedUpTo error: " + e.getMessage());
        }
        return unlocked;
    }
}
