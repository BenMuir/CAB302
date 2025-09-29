package com.typinggame.data;

import com.typinggame.model.Drill;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    // --- NEW METHODS for custom drills ---

    public int insertCustom(Drill d) {
        final String sql = "INSERT INTO drills(title, body, tier, is_custom, created_by, created_at) VALUES(?,?,?,?,?,?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.title);
            ps.setString(2, d.body);
            ps.setInt(3, d.tier);
            ps.setInt(4, 1);
            ps.setString(5, "local");
            ps.setLong(6, System.currentTimeMillis());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("insertCustom failed", e);
        }
    }

    public List<Drill> findCustom() {
        final String sql = "SELECT id, title, body, tier FROM drills WHERE is_custom=1 ORDER BY id DESC";
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
            throw new RuntimeException("findCustom failed", e);
        }
    }

    public boolean deleteCustom(int id) {
        final String sql = "DELETE FROM drills WHERE id=? AND is_custom=1";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("deleteCustom failed", e);
        }
    }
}
