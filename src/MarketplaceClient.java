package app;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MarketplaceClient {

    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {

        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("[CLIENT] Connected to server.");

            System.out.println(in.readLine());
            System.out.println(in.readLine());

            while (true) {
                System.out.print("> ");
                String command = scanner.nextLine();
                out.println(command);

                if (command.equalsIgnoreCase("EXIT")) {
                    System.out.println("[CLIENT] Disconnected.");
                    break;
                }

                String response;
                while ((response = in.readLine()) != null) {
                    if (response.isEmpty()) break;
                    System.out.println(response);

                    if (!in.ready()) break;
                }
            }

        } catch (IOException e) {
            System.out.println("[CLIENT] Error: " + e.getMessage());
        }
    }
}
