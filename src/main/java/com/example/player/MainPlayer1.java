package com.example.player;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * The MainPlayer1 class initializes Player1 and runs it in a separate process.
 */
public class MainPlayer1 {
    public static void main(String[] args) throws IOException {
        int messageLimit = 10;

        int player1Port = 5001;
        int player2Port = 5002;

        CountDownLatch serverReadyLatch = new CountDownLatch(1);

        try {
            new Player("Player1", player1Port, player2Port, messageLimit, serverReadyLatch).start();
        } catch (IOException | InterruptedException | BrokenBarrierException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}