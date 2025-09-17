package com.typinggame.data;

import com.typinggame.model.Drill;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DrillRepository {

    public List<Drill> findAll() {
        String sql = "SELECT id, title, body, tier FROM drills ORDER BY id";
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

    public List<Drill> findUpToTier(int maxTier) {
        String sql = "SELECT id, title, body, tier FROM drills WHERE tier <= ? ORDER BY tier, id";
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

    public int maxTier() {
        String sql = "SELECT COALESCE(MAX(tier), 1) AS mt FROM drills";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("mt") : 1;
        } catch (SQLException e) {
            throw new RuntimeException("maxTier failed", e);
        }
    }

    // seeding is done by Database at startup
}
