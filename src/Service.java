package model;

import model.exceptions.InvalidDataException;
import model.interfaces.Validatable;

import java.io.Serializable;

public class Service implements Validatable, Serializable {

    private long id;
    private long handymanId;
    private String title;
    private String description;
    private double price;
    private String category;
    private String city;
    private boolean active = true;

    public Service() {}

    public Service(long id, long handymanId, String title, String description,
                   double price, String category, String city) {
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
        if (title == null || title.isBlank()) throw new InvalidDataException("Title cannot be empty");
        if (city == null || city.isBlank()) throw new InvalidDataException("City cannot be empty");
        if (category == null || category.isBlank()) throw new InvalidDataException("Category cannot be empty");
        if (price <= 0) throw new InvalidDataException("Price must be positive");
    }

    public long getId() { return id; }
    public long getHandymanId() { return handymanId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public String getCity() { return city; }
    public boolean isActive() { return active; }

    public void setId(long id) { this.id = id; }
    public void setHandymanId(long handymanId) { this.handymanId = handymanId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setCity(String city) { this.city = city; }
    public void setActive(boolean active) { this.active = active; }
}
