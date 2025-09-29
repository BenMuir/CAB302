package com.typinggame.model;

import java.io.Serializable;

/**
 * Represents a player in the typing game.
 * Stores identifying information such as username and display name.
 */
public class User implements Serializable {

    // Unique identifier for this user
    public final int id;

    // Login or account name (used internally)
    public final String username;

    // Friendly name shown in the UI or leaderboard
    public final String displayName;

    /**
     * Creates a new user with the given details.
     *
     * @param id          unique user ID
     * @param username    internal username
     * @param displayName name shown to other players
     */
    public User(int id, String username, String displayName){
        this.id = id;
        this.username = username;
        this.displayName = displayName;
    }
}
