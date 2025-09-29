package com.typinggame.store;

import com.typinggame.model.*;

import java.io.*;
import java.nio.file.*;

/**
 * Handles saving and loading the game state from disk.
 * Uses Java serialization to persist the GameState object.
 */
public class DataStore {
    // File where game state is stored
    private static final String FILE_NAME = "typedb.ser";

    // In-memory game state
    private GameState state;

    /**
     * Creates a DataStore and loads state from file (or seeds a new one).
     */
    public DataStore(){ load(); }

    /**
     * Returns the current game state.
     */
    public GameState state(){ return state; }

    /**
     * Saves the current game state to disk.
     * This method is synchronized to prevent concurrent write issues.
     */
    public synchronized void save(){
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(Files.newOutputStream(Path.of(FILE_NAME)))){
            oos.writeObject(state);
        } catch(IOException e){
            throw new RuntimeException("Failed to save DB", e);
        }
    }

    /**
     * Loads the game state from disk.
     * If no file exists or loading fails, creates a new state, seeds it, and saves.
     */
    private void load(){
        Path p = Path.of(FILE_NAME);
        if(Files.exists(p)){
            try (ObjectInputStream ois =
                         new ObjectInputStream(Files.newInputStream(p))){
                this.state = (GameState) ois.readObject();
                return; // success
            } catch(Exception e){
                System.err.println("Failed to load DB, creating new. Reason: " + e.getMessage());
            }
        }

        // Create fresh state and seed demo data
        this.state = new GameState();
        seed();
        save();
    }

    /**
     * Seeds the database with an initial demo user and demo drills.
     * Only tier 1 drills are unlocked at the start.
     */
    private void seed(){
        // Demo user
        User u = new User(state.nextUserId++, "nash", "Nash");
        state.users.put(u.id, u);
        state.progress.put(u.id, new Progress(u.id));

        // Demo drills
        state.drills.put(1, new Drill(1, "Easy 1",
                "cat dog sun run fun", 1));
        state.drills.put(2, new Drill(2, "Easy 2",
                "time day night light bright", 1));
        state.drills.put(3, new Drill(3, "Easy 3",
                "red blue green yellow orange", 1));
        state.drills.put(4, new Drill(4, "Medium 1",
                "The quick brown fox jumps over the lazy dog.", 2));
        state.drills.put(5, new Drill(5, "Medium 2",
                "Typing fast is fun, but accuracy is even better!", 2));
        state.drills.put(6, new Drill(6, "Hard 1",
                "Complexity arises when we type: symbols, commas, and quotes-yet fluency must remain.", 3));

        // Unlock only tier 1 drills at the start
        Progress p = state.progress.get(u.id);
        p.unlockedDrillIds.add(1);
        p.unlockedDrillIds.add(2);
        p.unlockedDrillIds.add(3);
    }
}
