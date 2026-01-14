package app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MarketplaceServer {

    private static final int PORT = 5000;

    public static void main(String[] args) {
        System.out.println("[SERVER] Starting Handyman Marketplace Server on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SERVER] New client connected: " + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(clientSocket);
                handler.start();
            }

        } catch (IOException e) {
            System.out.println("[SERVER] Error: " + e.getMessage());
        }
    }
}
