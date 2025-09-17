package com.typinggame.model;

import java.io.Serializable;

public class Drill implements Serializable {
    public final int id;
    public final String title;
    public final String body;
    public final int tier;
    public String difficultyTier;

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
