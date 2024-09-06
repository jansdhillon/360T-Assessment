package com.example.player;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CountDownLatch;
/**
 * The Main class initializes two players and runs them in the same process.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        int messageLimit = 10;

        int player1Port = getAvailablePort();
        int player2Port = getAvailablePort();

        System.out.println("Player1 will use port: " + player1Port);
        System.out.println("Player2 will use port: " + player2Port);

        CountDownLatch serverReadyLatch1 = new CountDownLatch(1);
        CountDownLatch serverReadyLatch2 = new CountDownLatch(1);

        Thread t1 = new Thread(() -> {
            try {
                new Player("Player1", player1Port, player2Port, messageLimit, serverReadyLatch1).start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                new Player("Initiator", player2Port, player1Port, messageLimit, serverReadyLatch2).start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    private static int getAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}