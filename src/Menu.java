package app;

import model.*;
import model.enums.BookingStatus;
import model.enums.UserRole;
import model.exceptions.InvalidDataException;
import repository.BookingDAO;
import repository.ReviewDAO;
import repository.ServiceDAO;
import repository.UserDAO;
import util.IdGenerator;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Menu {

    private final Scanner scanner;
    private final UserDAO userDAO;
    private final ServiceDAO serviceDAO;
    private final BookingDAO bookingDAO;
    private final ReviewDAO reviewDAO;
    private final IdGenerator idGen;
    private User currentUser;

    public Menu(Scanner scanner) {
        this.scanner = scanner;
        this.userDAO = new UserDAO();
        this.serviceDAO = new ServiceDAO();
        this.bookingDAO = new BookingDAO();
        this.reviewDAO = new ReviewDAO();
        this.idGen = new IdGenerator(1000);
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
        System.out.println("\n=== Handyman Marketplace (MySQL) ===");
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
                System.out.println("Goodbye!");
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
            User user = new User(username, password, fullName, email, role); // rating defaults to 0.0
            user.validate();
            userDAO.insert(user);
            System.out.println("Registered successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("Role must be CUSTOMER or HANDYMAN.");
        } catch (InvalidDataException e) {
            System.out.println("Validation error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error while registering: " + e.getMessage());
        }
    }


    private void loginUser() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            User user = userDAO.findByUsernameAndPassword(username, password);
            if (user != null) {
                currentUser = user;
                System.out.println("Welcome, " + user.getFullName() + " (" + user.getRole() + ")");
            } else {
                System.out.println("Invalid credentials.");
            }
        } catch (SQLException e) {
            System.out.println("Database error during login: " + e.getMessage());
        }
    }

    private void listAllServicesPublic() {
        System.out.println("\n=== All Active Services ===");
        try {
            List<Service> services = serviceDAO.findAllActive();
            printServices(services);
        } catch (SQLException e) {
            System.out.println("Database error while listing services: " + e.getMessage());
        }
    }

    private void customerMenu() {
        System.out.println("\n=== Customer Menu ===");
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
        try {
            List<Service> services = serviceDAO.findByFilters(null, city);
            printServices(services);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void searchServices() {
        System.out.print("Enter category (Plumbing, Electrical, Cleaning, Painting or blank): ");
        String category = scanner.nextLine();
        if (category.isBlank()) category = null;
        System.out.print("Enter city (Cluj-Napoca, Timisoara, Bucharest or blank): ");
        String city = scanner.nextLine();
        if (city.isBlank()) city = null;

        try {
            List<Service> services = serviceDAO.findByFilters(category, city);
            printServices(services);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void makeBooking() {
        System.out.print("Enter service ID: ");
        long serviceId = readLong();
        try {
            Service service = serviceDAO.findById(serviceId);
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
            booking.validate();
            bookingDAO.insert(booking);
            System.out.println("Booking created with ID: " + booking.getId() + " (Status: " + booking.getStatus() + ")");
        } catch (InvalidDataException e) {
            System.out.println("Validation error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error while creating booking: " + e.getMessage());
        }
    }

    private void viewCustomerBookings() {
        System.out.println("Your bookings:");
        try {
            List<Booking> bookings = bookingDAO.findByCustomerId(currentUser.getId());
            for (Booking b : bookings) {
                Service s = serviceDAO.findById(b.getServiceId());
                System.out.println("ID: " + b.getId()
                        + " | Service: " + (s != null ? s.getTitle() : "?")
                        + " | Status: " + b.getStatus()
                        + " | Scheduled: " + b.getScheduledDateTime()
                        + " | Address: " + b.getAddress()
                        + " | Total: " + b.getTotalPrice());
            }
        } catch (SQLException e) {
            System.out.println("Database error while fetching bookings: " + e.getMessage());
        }
    }

    private void addReview() {
        System.out.print("Enter completed booking ID: ");
        long bookingId = readLong();
        try {
            Booking booking = bookingDAO.findById(bookingId);
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
            review.validate();
            reviewDAO.insert(review);
            System.out.println("Review saved with ID: " + review.getId());

            Service s = serviceDAO.findById(booking.getServiceId());
            if (s != null) {
                long handymanId = s.getHandymanId();
                double avg = reviewDAO.calculateHandymanAverageRating(handymanId);
                userDAO.updateRating(handymanId, avg);
                System.out.println("Handyman new average rating: " + avg);
            }
        } catch (InvalidDataException e) {
            System.out.println("Validation error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error while adding review: " + e.getMessage());
        }
    }

    private void handymanMenu() {
        System.out.println("\n=== Handyman Menu ===");
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
        System.out.print("Category: ");
        String category = scanner.nextLine();
        System.out.print("City: ");
        String city = scanner.nextLine();

        Service service = new Service(idGen.nextId(), currentUser.getId(), title, description, price, category, city);
        try {
            service.validate();
            serviceDAO.insert(service);
            System.out.println("Service added with ID: " + service.getId());
        } catch (InvalidDataException e) {
            System.out.println("Validation error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error while adding service: " + e.getMessage());
        }
    }

    private void viewHandymanBookings() {
        System.out.println("Your bookings:");
        try {
            List<Booking> bookings = bookingDAO.findByHandymanId(currentUser.getId());
            for (Booking b : bookings) {
                Service s = serviceDAO.findById(b.getServiceId());
                User customer = userDAO.findById(b.getCustomerId());
                System.out.println("ID: " + b.getId()
                        + " | Customer: " + (customer != null ? customer.getFullName() : "?")
                        + " | Service: " + (s != null ? s.getTitle() : "?")
                        + " | Status: " + b.getStatus()
                        + " | Scheduled: " + b.getScheduledDateTime()
                        + " | Address: " + b.getAddress()
                        + " | Total: " + b.getTotalPrice());
            }
        } catch (SQLException e) {
            System.out.println("Database error while fetching handyman bookings: " + e.getMessage());
        }
    }

    private void updateBookingStatusForHandyman(BookingStatus newStatus) {
        System.out.print("Enter booking ID: ");
        long bookingId = readLong();
        try {
            boolean updated = bookingDAO.updateStatusIfOwnedByHandyman(bookingId, currentUser.getId(), newStatus);
            if (updated) {
                System.out.println("Booking status updated to " + newStatus);
            } else {
                System.out.println("Booking not found or not assigned to you.");
            }
        } catch (SQLException e) {
            System.out.println("Database error while updating booking: " + e.getMessage());
        }
    }

    private void toggleServiceActive() {
        System.out.print("Enter service ID: ");
        long serviceId = readLong();
        try {
            boolean toggled = serviceDAO.toggleActiveForHandyman(serviceId, currentUser.getId());
            if (toggled) {
                System.out.println("Service active state toggled.");
            } else {
                System.out.println("Service not found or not yours.");
            }
        } catch (SQLException e) {
            System.out.println("Database error while toggling service: " + e.getMessage());
        }
    }

    private void adminMenu() {
        System.out.println("\n=== Admin Menu ===");
        System.out.println("1. List all users");
        System.out.println("2. List all services");
        System.out.println("3. Logout");
        System.out.print("Choose: ");
        int choice = readInt();

        try {
            switch (choice) {
                case 1 -> {
                    List<User> users = userDAO.findAll();
                    for (User u : users) {
                        System.out.println(u.getId() + " | " + u.getUsername() + " | " + u.getRole() + " | rating=" + u.getRating());
                    }
                }
                case 2 -> {
                    List<Service> services = serviceDAO.findAll();
                    printServices(services);
                }
                case 3 -> currentUser = null;
                default -> System.out.println("Invalid choice.");
            }
        } catch (SQLException e) {
            System.out.println("Database error in admin menu: " + e.getMessage());
        }
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

    private int readInt() {
        while (true) {
            try {
                int v = Integer.parseInt(scanner.nextLine());
                return v;
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid integer: ");
            }
        }
    }

    private long readLong() {
        while (true) {
            try {
                long v = Long.parseLong(scanner.nextLine());
                return v;
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid long: ");
            }
        }
    }

    private double readDouble() {
        while (true) {
            try {
                double v = Double.parseDouble(scanner.nextLine());
                return v;
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }
}
