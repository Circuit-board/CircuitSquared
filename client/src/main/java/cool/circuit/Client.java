package cool.circuit;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class Client {

    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;

        try (
                Socket socket = new Socket(host, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Scanner scanner = new Scanner(System.in)
        ) {

            while (true) {
                // Receive the latest game state
                String info = in.readLine(); // expects "players:P:0:0,P:1:1"
                if (info == null || !info.startsWith("players:")) continue;

                String data = info.substring("players:".length());
                String[] playerStrings = data.split(",");

                List<Player> players = new ArrayList<>();
                for (String playerStr : playerStrings) {
                    players.add(Player.fromString(playerStr, socket));
                }

                clearScreen();
                render(players);

                System.out.print("Move (w/a/s/d): ");

                if(scanner.hasNextLine()) {  // Check if there is a new line to read
                    String input = scanner.nextLine().trim().toLowerCase();

                    if (input.matches("[wasd]")) {
                        out.println(input); // Send to server
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void render(List<Player> players) {
        StringBuilder[] lines = {
                new StringBuilder("=========="),
                new StringBuilder("|        |"),
                new StringBuilder("|        |"),
                new StringBuilder("|        |"),
                new StringBuilder("==========")
        };

        for (Player player : players) {
            int x = player.getX();
            int y = player.getY();
            String character = player.getCharacter();

            if (y >= 0 && y < lines.length && x >= 0 && x < lines[y].length()) {
                lines[y].setCharAt(x, character.charAt(0));
            }
        }

        for (StringBuilder line : lines) {
            System.out.println(line);
        }
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
