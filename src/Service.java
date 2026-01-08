package model;

import java.io.Serializable;
import model.interfaces.Persistable;
import model.interfaces.Validatable;
import model.exceptions.InvalidDataException;

import java.io.*;

public class Service implements Validatable, Persistable, Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private long handymanId;
    private String title;
    private String description;
    private double price;
    private String category;
    private String city;
    private boolean active;

    public Service() {}

    public Service(long id, long handymanId, String title, String description, double price, String category, String city) {
        this.id = id;
        this.handymanId = handymanId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.city = city;
        this.active = true;
    }

    @Override
    public void validate() throws InvalidDataException {
        if (handymanId <= 0) throw new InvalidDataException("Handyman ID must be positive");
        if (title == null || title.trim().isEmpty()) throw new InvalidDataException("Service title cannot be empty");
        if (price <= 0) throw new InvalidDataException("Price must be positive");
        if (category == null || category.trim().isEmpty()) throw new InvalidDataException("Category cannot be empty");
        if (city == null || city.trim().isEmpty()) throw new InvalidDataException("City cannot be empty");
    }

    @Override
    public void saveToFile(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Failed to save Service: " + e.getMessage());
        }
    }

    @Override
    public void loadFromFile(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            Service loaded = (Service) ois.readObject();
            this.id = loaded.id;
            this.handymanId = loaded.handymanId;
            this.title = loaded.title;
            this.description = loaded.description;
            this.price = loaded.price;
            this.category = loaded.category;
            this.city = loaded.city;
            this.active = loaded.active;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load Service: " + e.getMessage());
        }
    }


    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getHandymanId() { return handymanId; }
    public void setHandymanId(long handymanId) { this.handymanId = handymanId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
