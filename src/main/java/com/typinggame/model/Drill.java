package com.typinggame.model;

import java.io.Serializable;

/**
 * Represents a typing drill (exercise).
 * Immutable fields for id, title, text, and level (1–10).
 * Backwards compatible with the old 'tier' field.
 */
public class Drill implements Serializable {
    public final int id;           // unique drill id
    public final String title;     // display title
    public final String body;      // sentence or text to type
    public final int level;        // Level 1–10 (replaces old 'tier')
    public final int tier;         // kept for backwards compatibility
    public String difficultyLabel; // optional extra label (e.g., "Level 3 – Drill 2")

    public Drill(int id, String title, String body, int level) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.level = level;
        this.tier = level; // mirror for backward compatibility
        this.difficultyLabel = "Level " + level;
    }

    /** Deprecated old constructor signature (tier). */
    @Deprecated
    public Drill(int id, String title, String body, int tier, boolean legacy) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.level = tier;
        this.tier = tier;
        this.difficultyLabel = "Level " + tier;
    }

    @Override
    public String toString() {
        return title + " (Level " + level + ")";
    }
}
