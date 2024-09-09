package com.example.player;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * The Main class initializes two players (Initiator and Player2) and runs them in the same process.
 * Responsibilities:
 * - Initializes and runs both players in separate threads.
 * - Dynamically allocates ports for the players.
 * - Waits for both players to complete the communication before exiting.
 * - Ensures the program terminates after the required number of messages have been sent and received.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        int messageLimit = 10;

        int player1Port = getAvailablePort();
        int player2Port = getAvailablePort();

        Thread t1 = new Thread(() -> {
            try {
                new Player("Initiator", player1Port, player2Port, messageLimit).start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                new Player("Player2", player2Port, player1Port, messageLimit).start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.exit(0);
    }

    private static int getAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
