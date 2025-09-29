package com.typinggame.model;

import java.io.Serializable;
import java.util.*;

/**
 * Represents the overall saved state of the typing game.
 * Stores users, drills, sessions, and progress data.
 */
public class GameState implements Serializable {

    // Counter used to assign unique IDs to new users
    public int nextUserId = 1;

    // Counter used to assign unique IDs to new sessions
    public int nextSessionId = 1;

    // All registered users, keyed by their unique user ID
    public final Map<Integer, User> users = new HashMap<>();

    // All drills available in the game, keyed by drill ID
    // LinkedHashMap keeps insertion order (useful for progression display)
    public final Map<Integer, Drill> drills = new LinkedHashMap<>();

    // All recorded typing sessions (each play attempt)
    public final List<Session> sessions = new ArrayList<>();

    // Progress records for each user, keyed by user ID
    public final Map<Integer, Progress> progress = new HashMap<>();
}
