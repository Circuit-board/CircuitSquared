package cool.circuit;

import java.net.Socket;

public abstract class Player {

    abstract void moveTo(final int x, final int y);

    abstract String getCharacter();

    abstract void setCharacter(final String character);

    abstract int getX();

    abstract int getY();

    abstract Socket getSocket();  // Add a getter for the Socket object

    @Override
    public String toString() {
        return getCharacter() + ":" + getX() + ":" + getY();
    }

    // Concrete PlayerImpl class that implements Player
    private static class PlayerImpl extends Player {

        private int x = 0;
        private int y = 0;
        private String character = "P";
        private Socket socket;  // Socket field to store the player's connection

        public PlayerImpl(Socket socket) {
            this.socket = socket;
        }

        @Override
        void moveTo(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        String getCharacter() {
            return character;
        }

        @Override
        void setCharacter(String character) {
            this.character = character;
        }

        @Override
        int getX() {
            return x;
        }

        @Override
        int getY() {
            return y;
        }

        @Override
        Socket getSocket() {
            return socket;  // Return the stored socket
        }
    }

    public static Player fromString(final String str, Socket socket) {
        String[] parts = str.split(":");
        if (parts.length != 3) throw new IllegalArgumentException("Invalid format: " + str);

        String character = parts[0];
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);

        PlayerImpl player = new PlayerImpl(socket);
        player.setCharacter(character);
        player.moveTo(x, y);
        return player;
    }
}
