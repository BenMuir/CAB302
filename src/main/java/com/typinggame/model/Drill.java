package com.typinggame.model;

import java.io.Serializable;

/**
 * Represents a typing drill (exercise).
 * Immutable fields for id, title, body text, and tier.
 */
public class Drill implements Serializable {
    public final int id;           // unique drill id
    public final String title;     // display title
    public final String body;      // sentence or text to type
    public final int tier;         // tier level (1 = easy, etc.)
    public String difficultyTier;  // optional/extra label if needed

    public Drill(int id, String title, String body, int tier) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.tier = tier;
    }

    @Override
    public String toString() {
        return title + " (Tier " + tier + ")";
    }
}
