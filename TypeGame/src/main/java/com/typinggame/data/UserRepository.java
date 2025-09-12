package com.typinggame.data;

import java.util.List;

/**
 * Defines the contract for user data persistence.
 * Implementations may use file storage, databases, or other mechanisms.
 *
 * [Ben M – Sept 10 2025]
 */
public interface UserRepository {

    /**
     * Saves the given user to persistent storage.
     *
     * @param user The user to save
     * @return true if the save was successful, false otherwise
     */
    boolean saveUser(User user);

    /**
     * Loads a user by their username.
     *
     * @param username The username of the user to load
     * @return The User object if found, or null if not found
     */
    User loadUser(String username);

    /**
     * Checks whether a user with the given username exists.
     *
     * @param username The username to check
     * @return true if the user exists, false otherwise
     */
    boolean userExists(String username);

    List<User> getAllUsers();

    /**
     * Retrieves all users from persistent storage.
     *
     * @return A list of all users
     */
    //List<User> getAllUsers();
}