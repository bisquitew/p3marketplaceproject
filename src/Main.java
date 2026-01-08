package app;

import model.*;
import model.enums.UserRole;
import model.exceptions.InvalidDataException;
import repository.InMemoryDatabase;
import util.IdGenerator;

import java.util.Scanner;

public class Main {
    private static final String USERS_PATH = "users.dat";

    public static void main(String[] args) {

        String mode = (args.length > 0) ? args[0].toLowerCase() : "default";
        System.out.println("Starting program in mode: " + mode);

        Scanner scanner = new Scanner(System.in);
        InMemoryDatabase db = new InMemoryDatabase();
        IdGenerator idGen = new IdGenerator(1000);

        switch (mode) {
            case "clean":
                System.out.println("Running in CLEAN mode ignoring saved users.");
                break;

            case "demo":
                System.out.println("Running in DEMO mode forcing demo data reload.");
                loadDemoData(db, idGen);
                break;

            case "default":
            default:
                System.out.println("Running in DEFAULT mode.");
                try {
                    db.loadUsers(USERS_PATH);
                    System.out.println("Users loaded from " + USERS_PATH);
                } catch (Exception e) {
                    System.out.println("No persisted users found or failed to load: " + e.getMessage());
                }
                break;
        }
        if (db.getServices().isEmpty()) {
            loadDemoData(db, idGen);
        }

        Menu menu = new Menu(scanner, db, idGen, USERS_PATH);
        menu.start();
    }

    private static void loadDemoData(InMemoryDatabase db, IdGenerator idGen) {
        if (db.getUsers().isEmpty()) {
            User admin = new User(idGen.nextId(), "admin", "admin123", "Admin User", "admin@market.ro", UserRole.ADMIN);
            User handyman1 = new User(idGen.nextId(), "plumberCluj", "pass123", "Ion Plumber", "ion@cluj.ro", UserRole.HANDYMAN);
            User handyman2 = new User(idGen.nextId(), "electricTimisoara", "pass123", "Mihai Electrician", "mihai@tm.ro", UserRole.HANDYMAN);
            User handyman3 = new User(idGen.nextId(), "painterBucharest", "pass123", "Andrei Painter", "andrei@buc.ro", UserRole.HANDYMAN);
            User handyman4 = new User(idGen.nextId(), "cleanerCluj", "pass123", "Maria Cleaner", "maria@cluj.ro", UserRole.HANDYMAN);
            User customer1 = new User(idGen.nextId(), "anaCustomer", "cust123", "Ana Customer", "ana@client.ro", UserRole.CUSTOMER);
            User customer2 = new User(idGen.nextId(), "paulCustomer", "cust456", "Paul Customer", "paul@client.ro", UserRole.CUSTOMER);

            try {
                admin.validate(); handyman1.validate(); handyman2.validate(); handyman3.validate(); handyman4.validate();
                customer1.validate(); customer2.validate();
            } catch (InvalidDataException e) {
                System.out.println("Validation error: " + e.getMessage());
            }

            db.getUsers().put(admin.getId(), admin);
            db.getUsers().put(handyman1.getId(), handyman1);
            db.getUsers().put(handyman2.getId(), handyman2);
            db.getUsers().put(handyman3.getId(), handyman3);
            db.getUsers().put(handyman4.getId(), handyman4);
            db.getUsers().put(customer1.getId(), customer1);
            db.getUsers().put(customer2.getId(), customer2);
        }

        Service service1 = new Service(idGen.nextId(), findUserIdByUsername(db, "plumberCluj"), "Fix leaking pipe", "Professional plumbing service", 150.0, "Plumbing", "Cluj-Napoca");
        Service service2 = new Service(idGen.nextId(), findUserIdByUsername(db, "electricTimisoara"), "Install light fixture", "Electrical installation and repair", 200.0, "Electrical", "Timisoara");
        Service service3 = new Service(idGen.nextId(), findUserIdByUsername(db, "painterBucharest"), "Wall painting", "Interior and exterior painting", 300.0, "Painting", "Bucharest");
        Service service4 = new Service(idGen.nextId(), findUserIdByUsername(db, "cleanerCluj"), "House cleaning", "Deep cleaning service", 100.0, "Cleaning", "Cluj-Napoca");
        Service service5 = new Service(idGen.nextId(), findUserIdByUsername(db, "electricTimisoara"), "Socket repair", "Repair/replace damaged wall sockets", 120.0, "Electrical", "Timisoara");

        try {
            service1.validate(); service2.validate(); service3.validate(); service4.validate(); service5.validate();
        } catch (InvalidDataException e) {
            System.out.println("Validation error: " + e.getMessage());
        }

        db.getServices().put(service1.getId(), service1);
        db.getServices().put(service2.getId(), service2);
        db.getServices().put(service3.getId(), service3);
        db.getServices().put(service4.getId(), service4);
        db.getServices().put(service5.getId(), service5);
    }

    private static long findUserIdByUsername(InMemoryDatabase db, String username) {
        return db.getUsers().values().stream()
                .filter(u -> username.equals(u.getUsername()))
                .map(User::getId)
                .findFirst()
                .orElse(-1L);
    }
}
