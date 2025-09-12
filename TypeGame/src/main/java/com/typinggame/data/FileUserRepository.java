package com.typinggame.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FileUserRepository implements UserRepository using local file storage.
 * Each user is serialized into a .dat file under the "users" directory.
 *
 * [Ben M – Sept 10 2025]
 */
public class FileUserRepository implements UserRepository {

    private final File userDir = new File("users");

    public FileUserRepository() {
        if (!userDir.exists()) {
            boolean created = userDir.mkdirs();
            if (!created) {
                System.err.println("Warning: Failed to create user directory at " + userDir.getAbsolutePath());
            }
        }
    }

    @Override
    public boolean saveUser(User user) {
        File file = new File(userDir, user.getUsername() + ".dat");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(user);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save user '" + user.getUsername() + "': " + e.getMessage());
            return false;
        }
    }

    @Override
    public User loadUser(String username) {
        File file = new File(userDir, username + ".dat");
        if (!file.exists()) {
            System.err.println("User file not found for: " + username);
            return null;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (User) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load user '" + username + "': " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean userExists(String username) {
        return new File(userDir, username + ".dat").exists();
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        File[] files = userDir.listFiles((dir, name) -> name.endsWith(".dat"));

        if (files == null) {
            System.err.println("User directory is empty or inaccessible.");
            return users;
        }

        for (File file : files) {
            String username = file.getName().replace(".dat", "");
            User user = loadUser(username);
            if (user != null) {
                users.add(user);
            }
        }

        return users;
    }
}