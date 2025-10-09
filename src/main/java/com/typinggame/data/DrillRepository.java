package com.typinggame.data;

import com.typinggame.model.Drill;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DrillRepository {

    // -----------------------------
    // Core (level-based) operations
    // -----------------------------

    /** Get all drills, ordered by (level, id). */
    public List<Drill> findAll() {
        final String sql = "SELECT id, title, body, level FROM drills ORDER BY level, id";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Drill> out = new ArrayList<>();
            while (rs.next()) out.add(row(rs));
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("findAll drills failed", e);
        }
    }

    /** Get drills with level <= maxLevel, ordered by (level, id). */
    public List<Drill> findUpToLevel(int maxLevel) {
        final String sql = "SELECT id, title, body, level FROM drills WHERE level <= ? ORDER BY level, id";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, maxLevel);
            try (ResultSet rs = ps.executeQuery()) {
                List<Drill> out = new ArrayList<>();
                while (rs.next()) out.add(row(rs));
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("findUpToLevel failed", e);
        }
    }

    /** Get exactly the drills for a specific level (expected 3). */
    public List<Drill> findByLevel(int level) {
        final String sql = "SELECT id, title, body, level FROM drills WHERE level = ? ORDER BY id";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, level);
            try (ResultSet rs = ps.executeQuery()) {
                List<Drill> out = new ArrayList<>();
                while (rs.next()) out.add(row(rs));
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByLevel failed", e);
        }
    }

    /** Find a drill by id. */
    public Optional<Drill> findById(int drillId) {
        final String sql = "SELECT id, title, body, level FROM drills WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, drillId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(row(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("findById failed", e);
        }
    }

    /** Highest level in the table (returns 1 if empty). */
    public int maxLevel() {
        final String sql = "SELECT COALESCE(MAX(level), 1) AS ml FROM drills";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("ml") : 1;
        } catch (SQLException e) {
            throw new RuntimeException("maxLevel failed", e);
        }
    }

    // -----------------------------
    // Custom drill operations
    // -----------------------------

    /** Insert a custom drill. Sets both 'level' and legacy 'tier' for compatibility. */
    public int insertCustom(Drill d) {
        String title = d.title == null ? "" : d.title.trim();
        String body = d.body == null ? "" : d.body.trim();
        int level = Math.max(1, d.level);              // prefer new field
        int tierCompat = Math.max(1, d.tier);          // mirror old field if used

        if (title.isEmpty()) throw new IllegalArgumentException("title must not be empty");
        if (body.isEmpty()) throw new IllegalArgumentException("content must not be empty");

        final String sql = "INSERT INTO drills(title, body, tier, level, is_custom, created_by, created_at) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setString(2, body);
            ps.setInt(3, tierCompat); // keep tier in sync for any legacy code/queries
            ps.setInt(4, level);
            ps.setInt(5, 1);          // is_custom
            ps.setString(6, "local");
            ps.setLong(7, System.currentTimeMillis());
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
        final String sql = "SELECT id, title, body, level FROM drills WHERE is_custom=1 ORDER BY id DESC";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Drill> out = new ArrayList<>();
            while (rs.next()) out.add(row(rs));
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

    // -----------------------------
    // Test helpers
    // -----------------------------

    /** Remove all drills (useful for test isolation). */
    public void clearAll() {
        try (Connection c = Database.getConnection();
             Statement st = c.createStatement()) {
            st.executeUpdate("DELETE FROM drills;");
            st.executeUpdate("DELETE FROM sqlite_sequence WHERE name='drills';");
        } catch (SQLException e) {
            throw new RuntimeException("clearAll failed", e);
        }
    }

    // -----------------------------
    // Backward-compat shims (tier)
    // -----------------------------

    /** Deprecated: use findUpToLevel. */
    @Deprecated
    public List<Drill> findUpToTier(int maxTier) {
        return findUpToLevel(maxTier);
    }

    /** Deprecated: use maxLevel. */
    @Deprecated
    public int maxTier() {
        return maxLevel();
    }

    // -----------------------------
    // Row mapper
    // -----------------------------
    private static Drill row(ResultSet rs) throws SQLException {
        // Uses the new Drill constructor that takes (id, title, body, level).
        return new Drill(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("body"),
                rs.getInt("level")
        );
    }
}
