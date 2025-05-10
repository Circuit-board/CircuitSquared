package cool.circuit;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {

    private static final List<Player> players = Collections.synchronizedList(new ArrayList<>());
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        int port = 12345;
        System.out.println("Starting server on port " + port + "...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Handle each client connection in a separate thread
                executorService.submit(() -> handleClient(clientSocket));
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
                Socket socket = clientSocket;
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            // Assign a new player
            Player player = Player.fromString("P:0:0", socket);
            synchronized (players) {
                players.add(player);
            }

            // Send the initial list of players to the client
            sendPlayerList(out);

            // Handle client input
            String line;
            while ((line = in.readLine()) != null) {
                // Move the player based on input
                switch(line) {
                    case "a" -> player.moveTo(player.getX() - 1, player.getY());
                    case "d" -> player.moveTo(player.getX() + 1, player.getY());
                    case "w" -> player.moveTo(player.getX(), player.getY() - 1);
                    case "s" -> player.moveTo(player.getX(), player.getY() + 1);
                }

                // After each move, send the updated list of players
                sendPlayerList(out);
            }

        } catch (IOException e) {
            System.err.println("Client handling error: " + e.getMessage());
        } finally {
            System.out.println("Client disconnected");

            // Remove player from the list when they disconnect
            synchronized (players) {
                players.removeIf(player -> player.getSocket().equals(clientSocket));
            }
        }
    }

    private static void sendPlayerList(PrintWriter out) {
        StringBuilder sb = new StringBuilder("players:");
        synchronized (players) {
            for (int i = 0; i < players.size(); i++) {
                sb.append(players.get(i).toString());
                if (i != players.size() - 1) {
                    sb.append(",");
                }
            }
        }
        out.println(sb.toString());
    }
}
