package com.example.player;

import java.io.IOException;
import java.net.ServerSocket;
/**
 * The Main class initializes two players and runs them in the same process.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        int messageLimit = 10;

        int player1Port = getAvailablePort();
        int player2Port = getAvailablePort();

        System.out.println("Initiator will use port: " + player1Port);
        System.out.println("Player2 will use port: " + player2Port);

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