package model;

import model.enums.UserRole;
import model.exceptions.InvalidDataException;
import model.interfaces.Validatable;

import java.io.Serializable;

public class User implements Validatable, Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private UserRole role;
    private double rating;

    public User(long id, String username, String password, String fullName, String email, UserRole role, double rating) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.rating = rating;
    }

    public User(String username, String password, String fullName, String email, UserRole role) {
        this(0, username, password, fullName, email, role, 0.0);
    }

    @Override
    public void validate() throws InvalidDataException {
        if (username == null || username.trim().isEmpty())
            throw new InvalidDataException("Username cannot be empty");
        if (password == null || password.trim().isEmpty())
            throw new InvalidDataException("Password cannot be empty");
        if (fullName == null || fullName.trim().isEmpty())
            throw new InvalidDataException("Full name cannot be empty");
        if (email == null || email.trim().isEmpty())
            throw new InvalidDataException("Email cannot be empty");
        if (role == null)
            throw new InvalidDataException("Role must be set");
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
