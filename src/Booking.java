package model;

import java.io.Serializable;
import java.time.LocalDateTime;

import model.enums.BookingStatus;
import model.interfaces.Persistable;
import model.interfaces.Validatable;
import model.exceptions.InvalidDataException;

import java.io.*;

public class Booking implements Validatable, Persistable, Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private long customerId;
    private long serviceId;
    private LocalDateTime scheduledDateTime;
    private BookingStatus status;
    private String address;
    private double totalPrice;

    public Booking() {}

    public Booking(long id, long customerId, long serviceId, LocalDateTime scheduledDateTime, String address, double totalPrice) {
        this.id = id;
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.scheduledDateTime = scheduledDateTime;
        this.status = BookingStatus.PENDING;
        this.address = address;
        this.totalPrice = totalPrice;
    }

    @Override
    public void validate() throws InvalidDataException {
        if (customerId <= 0) throw new InvalidDataException("Customer ID must be positive");
        if (serviceId <= 0) throw new InvalidDataException("Service ID must be positive");
        if (scheduledDateTime == null) throw new InvalidDataException("Scheduled date cannot be null");
        if (address == null || address.trim().isEmpty()) throw new InvalidDataException("Address cannot be empty");
        if (totalPrice <= 0) throw new InvalidDataException("Total price must be positive");
        if (status == null) throw new InvalidDataException("Booking status must be set");
    }

    @Override
    public void saveToFile(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Failed to save Booking: " + e.getMessage());
        }
    }

    @Override
    public void loadFromFile(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            Booking loaded = (Booking) ois.readObject();
            this.id = loaded.id;
            this.customerId = loaded.customerId;
            this.serviceId = loaded.serviceId;
            this.scheduledDateTime = loaded.scheduledDateTime;
            this.status = loaded.status;
            this.address = loaded.address;
            this.totalPrice = loaded.totalPrice;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load Booking: " + e.getMessage());
        }
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getCustomerId() { return customerId; }
    public void setCustomerId(long customerId) { this.customerId = customerId; }

    public long getServiceId() { return serviceId; }
    public void setServiceId(long serviceId) { this.serviceId = serviceId; }

    public LocalDateTime getScheduledDateTime() { return scheduledDateTime; }
    public void setScheduledDateTime(LocalDateTime scheduledDateTime) { this.scheduledDateTime = scheduledDateTime; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
}
