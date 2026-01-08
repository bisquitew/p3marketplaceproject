package app;

import model.*;
import model.enums.*;
import model.exceptions.InvalidDataException;
import repository.InMemoryDatabase;
import util.IdGenerator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Menu {
    private final Scanner scanner;
    private final InMemoryDatabase db;
    private final IdGenerator idGen;
    private final String usersPath;
    private User currentUser;

    public Menu(Scanner scanner, InMemoryDatabase db, IdGenerator idGen, String usersPath) {
        this.scanner = scanner;
        this.db = db;
        this.idGen = idGen;
        this.usersPath = usersPath;
    }

    public void start() {
        while (true) {
            try {
                if (currentUser == null) {
                    showWelcomeMenu();
                } else {
                    switch (currentUser.getRole()) {
                        case CUSTOMER -> customerMenu();
                        case HANDYMAN -> handymanMenu();
                        case ADMIN -> adminMenu();
                    }
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input type. Please enter the correct value.");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }
    private void showWelcomeMenu() {
        System.out.println("\n Handyman Marketplace ");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. List all services");
        System.out.println("4. Exit");
        System.out.print("Choose: ");
        int choice = readInt();

        switch (choice) {
            case 1 -> registerUser();
            case 2 -> loginUser();
            case 3 -> listAllServicesPublic();
            case 4 -> {
                persistUsers();
                System.exit(0);
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    private void registerUser() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Full name: ");
        String fullName = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Role (CUSTOMER/HANDYMAN): ");
        String roleStr = scanner.nextLine().toUpperCase();

        try {
            UserRole role = UserRole.valueOf(roleStr);
            User user = new User(idGen.nextId(), username, password, fullName, email, role);
            user.validate();
            db.getUsers().put(user.getId(), user);
            persistUsers();
            System.out.println("Registered successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("Role must be CUSTOMER or HANDYMAN.");
        } catch (InvalidDataException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void loginUser() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        for (User user : db.getUsers().values()) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                currentUser = user;
                System.out.println("Welcome, " + user.getFullName() + " (" + user.getRole() + ")");
                return;
            }
        }
        System.out.println("Invalid credentials.");
    }

    private void listAllServicesPublic() {
        System.out.println("\n All Services ");
        List<Service> all = new ArrayList<>(db.getServices().values());
        all.sort(Comparator.comparing(Service::getCity).thenComparing(Service::getCategory));
        printServices(all);
    }

    private void customerMenu() {
        System.out.println("\n Customer Menu ");
        System.out.println("1. View services by city");
        System.out.println("2. Search services by category and city");
        System.out.println("3. Make a booking");
        System.out.println("4. View my bookings");
        System.out.println("5. Add review to completed booking");
        System.out.println("6. Logout");
        System.out.print("Choose: ");
        int choice = readInt();

        switch (choice) {
            case 1 -> viewServicesByCity();
            case 2 -> searchServices();
            case 3 -> makeBooking();
            case 4 -> viewCustomerBookings();
            case 5 -> addReview();
            case 6 -> currentUser = null;
            default -> System.out.println("Invalid choice.");
        }
    }

    private void viewServicesByCity() {
        System.out.print("Enter city: ");
        String city = scanner.nextLine();
        List<Service> services = db.findServicesByCategoryAndCity(null, city);
        printServices(services);
    }

    private void searchServices() {
        System.out.print("Enter category (Plumbing, Electrical, Cleaning, Painting or blank): ");
        String category = scanner.nextLine();
        if (category.isBlank()) category = null;
        System.out.print("Enter city (Cluj-Napoca, Timisoara, Bucharest or blank): ");
        String city = scanner.nextLine();
        if (city.isBlank()) city = null;

        List<Service> services = db.findServicesByCategoryAndCity(category, city);
        printServices(services);
    }

    private void makeBooking() {
        System.out.print("Enter service ID: ");
        long serviceId = readLong();
        Service service = db.getServices().get(serviceId);
        if (service == null || !service.isActive()) {
            System.out.println("Service not found or inactive.");
            return;
        }

        System.out.print("Enter address: ");
        String address = scanner.nextLine();

        System.out.print("Enter scheduled date time (YYYY-MM-DDTHH:MM), e.g., 2025-12-01T10:30: ");
        String dt = scanner.nextLine();
        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(dt);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date-time format. Using default tomorrow 10:00.");
            dateTime = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        }

        Booking booking = new Booking(idGen.nextId(), currentUser.getId(), serviceId, dateTime, address, service.getPrice());
        try {
            booking.validate();
            db.getBookings().put(booking.getId(), booking);
            System.out.println("Booking created with ID: " + booking.getId() + " (Status: " + booking.getStatus() + ")");
        } catch (InvalidDataException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewCustomerBookings() {
        System.out.println("Your bookings:");
        db.getBookings().values().stream()
                .filter(b -> b.getCustomerId() == currentUser.getId())
                .forEach(b -> {
                    Service s = db.getServices().get(b.getServiceId());
                    System.out.println("ID: " + b.getId()
                            + " | Service: " + (s != null ? s.getTitle() : "?")
                            + " | Status: " + b.getStatus()
                            + " | Scheduled: " + b.getScheduledDateTime()
                            + " | Address: " + b.getAddress()
                            + " | Total: " + b.getTotalPrice());
                });
    }

    private void addReview() {
        System.out.print("Enter completed booking ID: ");
        long bookingId = readLong();
        Booking booking = db.getBookings().get(bookingId);
        if (booking == null || booking.getCustomerId() != currentUser.getId()) {
            System.out.println("Booking not found or not yours.");
            return;
        }
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            System.out.println("Booking is not completed yet.");
            return;
        }

        System.out.print("Rating (1-5): ");
        int rating = readInt();
        scanner.nextLine();
        System.out.print("Comment: ");
        String comment = scanner.nextLine();

        Review review = new Review(idGen.nextId(), bookingId, rating, comment);
        try {
            review.validate();
            db.getReviews().put(review.getId(), review);
            System.out.println("Review saved with ID: " + review.getId());

            Service s = db.getServices().get(booking.getServiceId());
            if (s != null) {
                long handymanId = s.getHandymanId();
                double avg = calculateHandymanAverageRating(handymanId);
                User handyman = db.getUsers().get(handymanId);
                if (handyman != null) handyman.setRating(avg);
                System.out.println("Handyman new average rating: " + avg);
            }
        } catch (InvalidDataException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private double calculateHandymanAverageRating(long handymanId) {
        List<Long> bookingIds = new ArrayList<>();
        for (Booking b : db.getBookings().values()) {
            Service s = db.getServices().get(b.getServiceId());
            if (s != null && s.getHandymanId() == handymanId && b.getStatus() == BookingStatus.COMPLETED) {
                bookingIds.add(b.getId());
            }
        }
        int sum = 0;
        int count = 0;
        for (Review r : db.getReviews().values()) {
            if (bookingIds.contains(r.getBookingId())) {
                sum += r.getRating();
                count++;
            }
        }
        return count == 0 ? 0.0 : Math.round((sum * 1.0 / count) * 100.0) / 100.0;
    }

    private void printServices(List<Service> services) {
        if (services == null || services.isEmpty()) {
            System.out.println("No services found.");
            return;
        }
        for (Service s : services) {
            System.out.println(s.getId() + ": " + s.getTitle()
                    + " | " + s.getCategory()
                    + " | " + s.getCity()
                    + " | " + s.getPrice() + " RON"
                    + " | Active: " + (s.isActive() ? "yes" : "no")
                    + " | HandymanId: " + s.getHandymanId());
        }
    }

    private void handymanMenu() {
        System.out.println("\n Handyman Menu ");
        System.out.println("1. Add service");
        System.out.println("2. View my bookings");
        System.out.println("3. Accept booking");
        System.out.println("4. Complete booking");
        System.out.println("5. Cancel booking");
        System.out.println("6. Deactivate/Activate my service");
        System.out.println("7. Logout");
        System.out.print("Choose: ");
        int choice = readInt();

        switch (choice) {
            case 1 -> addService();
            case 2 -> viewHandymanBookings();
            case 3 -> updateBookingStatusForHandyman(BookingStatus.ACCEPTED);
            case 4 -> updateBookingStatusForHandyman(BookingStatus.COMPLETED);
            case 5 -> updateBookingStatusForHandyman(BookingStatus.CANCELLED);
            case 6 -> toggleServiceActive();
            case 7 -> currentUser = null;
            default -> System.out.println("Invalid choice.");
        }
    }

    private void addService() {
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Price: ");
        double price = readDouble();
        scanner.nextLine();
        System.out.print("Category (Plumbing/Electrical/Cleaning/Painting): ");
        String category = scanner.nextLine();
        System.out.print("City (Cluj-Napoca/Timisoara/Bucharest): ");
        String city = scanner.nextLine();

        Service service = new Service(idGen.nextId(), currentUser.getId(), title, description, price, category, city);
        try {
            service.validate();
            db.getServices().put(service.getId(), service);
            System.out.println("Service added with ID: " + service.getId());
        } catch (InvalidDataException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewHandymanBookings() {
        System.out.println("Your bookings:");
        for (Booking b : db.getBookings().values()) {
            Service s = db.getServices().get(b.getServiceId());
            if (s != null && s.getHandymanId() == currentUser.getId()) {
                System.out.println("Booking ID: " + b.getId()
                        + " | Status: " + b.getStatus()
                        + " | Address: " + b.getAddress()
                        + " | Scheduled: " + b.getScheduledDateTime()
                        + " | Total: " + b.getTotalPrice());
            }
        }
    }

    private void updateBookingStatusForHandyman(BookingStatus newStatus) {
        System.out.print("Enter booking ID: ");
        long bookingId = readLong();
        Booking b = db.getBookings().get(bookingId);
        if (b == null) {
            System.out.println("Booking not found.");
            return;
        }
        Service s = db.getServices().get(b.getServiceId());
        if (s == null || s.getHandymanId() != currentUser.getId()) {
            System.out.println("This booking does not belong to your services.");
            return;
        }
        b.setStatus(newStatus);
        System.out.println("Booking " + b.getId() + " set to " + newStatus);
    }

    private void toggleServiceActive() {
        System.out.print("Enter your service ID: ");
        long serviceId = readLong();
        Service s = db.getServices().get(serviceId);
        if (s == null || s.getHandymanId() != currentUser.getId()) {
            System.out.println("Service not found or not yours.");
            return;
        }
        s.setActive(!s.isActive());
        System.out.println("Service " + s.getId() + " is now " + (s.isActive() ? "Active" : "Inactive"));
    }

    private void adminMenu() {
        System.out.println("\n Admin Menu ");
        System.out.println("1. List all users");
        System.out.println("2. List all services");
        System.out.println("3. List all bookings");
        System.out.println("4. Disable a user");
        System.out.println("5. Deactivate a service");
        System.out.println("6. Logout");
        System.out.print("Choose: ");
        int choice = readInt();

        switch (choice) {
            case 1 -> listUsers();
            case 2 -> listServices();
            case 3 -> listBookings();
            case 4 -> disableUser();
            case 5 -> deactivateService();
            case 6 -> currentUser = null;
            default -> System.out.println("Invalid choice.");
        }
    }

    private void listUsers() {
        System.out.println("Users:");
        for (User u : db.getUsers().values()) {
            System.out.println(u.getId() + ": " + u.getUsername() + " (" + u.getRole() + ") rating=" + u.getRating());
        }
    }

    private void listServices() {
        System.out.println("Services:");
        for (Service s : db.getServices().values()) {
            System.out.println(s.getId() + ": " + s.getTitle() + " - " + s.getCity()
                    + " | " + s.getCategory()
                    + " | " + s.getPrice() + " RON"
                    + " | Active: " + s.isActive()
                    + " | HandymanId: " + s.getHandymanId());
        }
    }

    private void listBookings() {
        System.out.println("Bookings:");
        for (Booking b : db.getBookings().values()) {
            Service s = db.getServices().get(b.getServiceId());
            User cust = db.getUsers().get(b.getCustomerId());
            System.out.println("Booking " + b.getId()
                    + " | Service: " + (s != null ? s.getTitle() : "?")
                    + " | Customer: " + (cust != null ? cust.getUsername() : "?")
                    + " | Status: " + b.getStatus()
                    + " | Scheduled: " + b.getScheduledDateTime()
                    + " | Address: " + b.getAddress());
        }
    }

    private void disableUser() {
        System.out.print("Enter user ID to disable: ");
        long userId = readLong();
        User u = db.getUsers().get(userId);
        if (u == null) {
            System.out.println("User not found.");
            return;
        }
        db.getUsers().remove(userId);
        persistUsers();
        System.out.println("User " + userId + " disabled (removed).");
    }

    private void deactivateService() {
        System.out.print("Enter service ID to deactivate: ");
        long serviceId = readLong();
        Service s = db.getServices().get(serviceId);
        if (s == null) {
            System.out.println("Service not found.");
            return;
        }
        s.setActive(false);
        System.out.println("Service " + serviceId + " deactivated.");
    }

    private void persistUsers() {
        try {
            db.saveUsers(usersPath);
            System.out.println("Users saved.");
        } catch (IOException e) {
            System.out.println("Failed to save users: " + e.getMessage());
        }
    }

    private int readInt() {
        while (true) {
            try {
                String line = scanner.nextLine();
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid integer: ");
            }
        }
    }

    private long readLong() {
        while (true) {
            try {
                String line = scanner.nextLine();
                return Long.parseLong(line.trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid long number: ");
            }
        }
    }

    private double readDouble() {
        while (true) {
            try {
                String line = scanner.nextLine();
                return Double.parseDouble(line.trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
}
