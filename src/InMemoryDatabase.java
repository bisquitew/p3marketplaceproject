package repository;

import model.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDatabase {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Map<Long, Service> services = new ConcurrentHashMap<>();
    private final Map<Long, Booking> bookings = new ConcurrentHashMap<>();
    private final Map<Long, Review> reviews = new ConcurrentHashMap<>();
    private final Map<Long, List<Message>> messagesByBooking = new ConcurrentHashMap<>();

    public Map<Long, User> getUsers() { return users; }
    public Map<Long, Service> getServices() { return services; }
    public Map<Long, Booking> getBookings() { return bookings; }
    public Map<Long, Review> getReviews() { return reviews; }
    public Map<Long, List<Message>> getMessagesByBooking() { return messagesByBooking; }

    public List<Service> findServicesByCategoryAndCity(String category, String city) {
        List<Service> result = new ArrayList<>();
        for (Service s : services.values()) {
            if (s.isActive()
                    && (category == null || s.getCategory().equalsIgnoreCase(category))
                    && (city == null || s.getCity().equalsIgnoreCase(city))) {
                result.add(s);
            }
        }
        return result;
    }

    public void addMessage(Message message) {
        messagesByBooking.computeIfAbsent(message.getBookingId(), k -> new ArrayList<>()).add(message);
    }

    public void saveUsers(String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(new HashMap<>(users));
        }
    }

    @SuppressWarnings("unchecked")
    public void loadUsers(String filePath) throws IOException, ClassNotFoundException {
        File f = new File(filePath);
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            Map<Long, User> loaded = (Map<Long, User>) ois.readObject();
            users.clear();
            users.putAll(loaded);
        }
    }
}
