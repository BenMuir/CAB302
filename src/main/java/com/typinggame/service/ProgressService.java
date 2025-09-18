package com.typinggame.service;

import com.typinggame.data.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Tier progression rules:
 *  - Start at Tier 1
 *  - Tier 2 unlocks when ALL Tier 1 drills have ≥1 session
 *  - Tier 3 unlocks when ALL Tier 2 drills have ≥1 session
 */
public final class ProgressService {

    // Stateless; queries DB each call
    public ProgressService() {}

    /** Returns unlocked tier for the user: 1..3. */
    public int currentUnlockedTier(int userId) {
        if (userId <= 0) return 1;

        boolean tier1Done = hasCompletedAllDrillsInTier(userId, 1);
        if (!tier1Done) {
            debug(userId, 1);
            return 1;
        }

        boolean tier2Done = hasCompletedAllDrillsInTier(userId, 2);
        if (!tier2Done) {
            debug(userId, 2);
            return 2;
        }

        debug(userId, 3);
        return 3;
    }

    /** True if the user has at least one session for every drill in the tier. */
    public boolean hasCompletedAllDrillsInTier(int userId, int tier) {
        int total = totalDrillsInTier(tier);
        if (total == 0) return false; // no drills → can’t complete
        int distinctCompleted = distinctCompletedInTier(userId, tier);
        return distinctCompleted >= total;
    }

    /** Count drills that exist in a tier. */
    private int totalDrillsInTier(int tier) {
        String sql = "SELECT COUNT(*) AS cnt FROM drills WHERE tier = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, tier);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cnt");
            }
        } catch (SQLException e) {
            throw new RuntimeException("totalDrillsInTier failed", e);
        }
        return 0;
    }

    /** Count DISTINCT drills the user has completed in a tier (any score counts). */
    public int distinctCompletedInTier(int userId, int tier) {
        String sql = """
            SELECT COUNT(DISTINCT s.drill_id) AS cnt
            FROM sessions s
            JOIN drills d ON d.id = s.drill_id
            WHERE s.user_id = ? AND d.tier = ?
        """;
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, tier);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cnt");
            }
        } catch (SQLException e) {
            throw new RuntimeException("distinctCompletedInTier failed", e);
        }
        return 0;
    }

    /** Log simple progress info (best-effort; ignores errors). */
    private void debug(int userId, int upToTier) {
        try {
            int t1Total = totalDrillsInTier(1);
            int t1Done  = distinctCompletedInTier(userId, 1);
            int t2Total = totalDrillsInTier(2);
            int t2Done  = distinctCompletedInTier(userId, 2);
            System.out.println("[Progress] user=" + userId +
                    " | Tier1 " + t1Done + "/" + t1Total +
                    " | Tier2 " + t2Done + "/" + t2Total +
                    " | returning upTo=" + upToTier);
        } catch (Exception ignore) {}
    }
}
