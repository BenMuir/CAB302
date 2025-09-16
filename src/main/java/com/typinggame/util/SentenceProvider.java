package com.typinggame.util;

import com.typinggame.data.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Supplies target text. Tries DB first; falls back to a small built-in list.
 * Works with your existing controller method: SentenceProvider.getSentence()
 */
public final class SentenceProvider {
    private SentenceProvider() {}

    public static String getSentence() {
        // Try DB first
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT body FROM drills ORDER BY id");
             ResultSet rs = ps.executeQuery()) {

            java.util.ArrayList<String> bodies = new java.util.ArrayList<>();
            while (rs.next()) bodies.add(rs.getString(1));

            if (!bodies.isEmpty()) {
                int i = ThreadLocalRandom.current().nextInt(bodies.size());
                return bodies.get(i);
            }
        } catch (Exception e) {
            System.err.println("SentenceProvider DB fetch failed, using fallback: " + e.getMessage());
        }

        // Fallback (same 6 drills)
        List<String> fallback = List.of(
                "cat dog sun run fun",
                "time day night light bright",
                "red blue green yellow orange",
                "The quick brown fox jumps over the lazy dog.",
                "Typing fast is fun, but accuracy is even better!",
                "Complexity arises when we type: symbols, commas, and quotes—yet fluency must remain."
        );
        return fallback.get(ThreadLocalRandom.current().nextInt(fallback.size()));
    }
}
