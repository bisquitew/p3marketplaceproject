package model;

import java.io.Serializable;

import model.interfaces.Persistable;
import model.interfaces.Validatable;
import model.exceptions.InvalidDataException;

import java.io.*;

public class Review implements Validatable, Persistable, Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private long bookingId;
    private int rating; // 1 to 5
    private String comment;

    public Review() {}

    public Review(long id, long bookingId, int rating, String comment) {
        this.id = id;
        this.bookingId = bookingId;
        this.rating = rating;
        this.comment = comment;
    }

    @Override
    public void validate() throws InvalidDataException {
        if (bookingId <= 0) throw new InvalidDataException("Booking ID must be positive");
        if (rating < 1 || rating > 5) throw new InvalidDataException("Rating must be between 1 and 5");
        if (comment == null) comment = "";
    }

    @Override
    public void saveToFile(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Failed to save Review: " + e.getMessage());
        }
    }

    @Override
    public void loadFromFile(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            Review loaded = (Review) ois.readObject();
            this.id = loaded.id;
            this.bookingId = loaded.bookingId;
            this.rating = loaded.rating;
            this.comment = loaded.comment;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load Review: " + e.getMessage());
        }
    }


    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getBookingId() { return bookingId; }
    public void setBookingId(long bookingId) { this.bookingId = bookingId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
