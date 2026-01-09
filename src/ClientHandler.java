package app;

import repository.UserDAO;
import repository.ServiceDAO;
import model.User;
import model.Service;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class ClientHandler extends Thread {

    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private final UserDAO userDAO = new UserDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Welcome to Handyman Marketplace Server!");
            out.println("Commands: LOGIN <user> <pass>, LIST_SERVICES, EXIT");

            String line;

            while ((line = in.readLine()) != null) {

                String[] parts = line.split(" ");

                if (parts[0].equalsIgnoreCase("LOGIN")) {
                    handleLogin(parts);

                } else if (parts[0].equalsIgnoreCase("LIST_SERVICES")) {
                    handleListServices();

                } else if (parts[0].equalsIgnoreCase("EXIT")) {
                    out.println("Goodbye!");
                    break;

                } else {
                    out.println("Unknown command.");
                }
            }

        } catch (IOException e) {
            System.out.println("[SERVER] Client disconnected.");
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    private void handleLogin(String[] parts) {
        if (parts.length < 3) {
            out.println("Usage: LOGIN <username> <password>");
            return;
        }

        String username = parts[1];
        String password = parts[2];

        try {
            User user = userDAO.findByUsernameAndPassword(username, password);
            if (user != null) {
                out.println("LOGIN_SUCCESS " + user.getFull_name() + " (" + user.getRole() + ")");
            } else {
                out.println("LOGIN_FAILED");
            }
        } catch (SQLException e) {
            out.println("ERROR: " + e.getMessage());
        }
    }

    private void handleListServices() {
        try {
            List<Service> services = serviceDAO.findAllActive();
            if (services.isEmpty()) {
                out.println("No active services.");
                return;
            }

            out.println("=== Active Services ===");
            for (Service s : services) {
                out.println(s.getId() + " | " + s.getTitle() + " | " + s.getCity() + " | " + s.getPrice());
            }

        } catch (SQLException e) {
            out.println("ERROR: " + e.getMessage());
        }
    }
}
