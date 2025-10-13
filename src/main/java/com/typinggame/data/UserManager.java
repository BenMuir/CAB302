package com.typinggame.data;

/**
 * yer so this class manages user classes. is unique within the program and stored in the app context so that dupes aren
 * 't made
 */
public class UserManager {
    private final UserRepository userRepo;
    private User currentUser;

    /**
     * initialises the user manager for the given repo
     * @param repo repository to be used by the user manager
     */
    public UserManager(UserRepository repo) {
        this.userRepo = repo;
    }

    /**
     * registers a new user to the database
     * @param username username
     * @param password unhashed password
     * @return boolean based on whether the operation was successful
     */
    public boolean register(String username, String password) {
        if (userRepo.userExists(username)) return false;
        String hashedPassword = hash(password);
        User newUser = new User(username, hashedPassword);
        boolean success = userRepo.saveUser(newUser);
        if (success) currentUser = newUser;
        return success;
    }

    /**
     * logs a user in
     * @param username ubeser name of the user
     * @param password jpassword of the user
     * @return returns whether successful
     */
    public boolean login(String username, String password) {
        User user = userRepo.loadUser(username);
        if (user == null || !user.getPasswordHash().equals(hash(password))) return false;

        currentUser = user;
        return true;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void saveCurrentUser() {
        if (currentUser != null) {
            userRepo.saveUser(currentUser);
        }
    }

    /**
     * Hashes da password
     * @param password raw input
     * @return hashed string
     */
    private String hash(String password) {
        // Simple hash for now — replace with SHA-256 or bcrypt later
        return Integer.toHexString(password.hashCode());
    }
}