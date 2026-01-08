package model;

import java.io.Serializable;
import model.enums.UserRole;
import model.interfaces.Persistable;
import model.interfaces.Validatable;
import model.exceptions.InvalidDataException;

import java.io.*;

public class User implements Validatable, Persistable, Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private UserRole role;
    private double rating; // average rating for HANDYMAN, 0 for others

    public User() {}

    public User(long id, String username, String password, String fullName, String email, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.rating = 0.0;
    }

    @Override
    public void validate() throws InvalidDataException {
        if (username == null || username.trim().isEmpty()) throw new InvalidDataException("Username cannot be empty");
        if (password == null || password.length() < 4) throw new InvalidDataException("Password must be at least 4 characters");
        if (fullName == null || fullName.trim().isEmpty()) throw new InvalidDataException("Full name cannot be empty");
        if (email == null || !email.contains("@") || !email.contains(".")) throw new InvalidDataException("Invalid email");
        if (role == null) throw new InvalidDataException("User role must be set");
        if (rating < 0 || rating > 5) throw new InvalidDataException("Rating must be between 0 and 5");
    }

    @Override
    public void saveToFile(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Failed to save User: " + e.getMessage());
        }
    }

    @Override
    public void loadFromFile(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            User loaded = (User) ois.readObject();
            this.id = loaded.id;
            this.username = loaded.username;
            this.password = loaded.password;
            this.fullName = loaded.fullName;
            this.email = loaded.email;
            this.role = loaded.role;
            this.rating = loaded.rating;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load User: " + e.getMessage());
        }
    }


    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
}
