package com.typinggame.data;

public class UserManager {
    private final UserRepository userRepo;
    private User currentUser;

    public UserManager(UserRepository repo) {
        this.userRepo = repo;
    }

    public boolean register(String username, String password) {
        if (userRepo.userExists(username)) return false;
        String hashedPassword = hash(password);
        User newUser = new User(username, hashedPassword);
        boolean success = userRepo.saveUser(newUser);
        if (success) currentUser = newUser;
        return success;
    }

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

    private String hash(String password) {
        // Simple hash for now — replace with SHA-256 or bcrypt later
        return Integer.toHexString(password.hashCode());
    }
}