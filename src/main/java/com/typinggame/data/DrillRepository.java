package com.typinggame.data;

import com.typinggame.model.Drill;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple read-only access to drills in the DB.
 * - Uses try-with-resources so connections are closed.
 * - Wraps SQL errors in RuntimeException.
 */
public class DrillRepository {

    /** Get all drills, ordered by id. */
    public List<Drill> findAll() {
        final String sql = "SELECT id, title, body, tier FROM drills ORDER BY id";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Drill> out = new ArrayList<>();
            while (rs.next()) {
                out.add(new Drill(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("body"),
                        rs.getInt("tier")
                ));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("findAll drills failed", e);
        }
    }

    /** Get drills with tier <= maxTier, ordered by tier then id. */
    public List<Drill> findUpToTier(int maxTier) {
        final String sql = "SELECT id, title, body, tier FROM drills WHERE tier <= ? ORDER BY tier, id";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, maxTier);
            try (ResultSet rs = ps.executeQuery()) {
                List<Drill> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new Drill(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("body"),
                            rs.getInt("tier")
                    ));
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("findUpToTier failed", e);
        }
    }

    /** Highest tier in the table (returns 1 if empty). */
    public int maxTier() {
        final String sql = "SELECT COALESCE(MAX(tier), 1) AS mt FROM drills";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt("mt") : 1;
        } catch (SQLException e) {
            throw new RuntimeException("maxTier failed", e);
        }
    }

    // Seeding happens at Database startup.
}
