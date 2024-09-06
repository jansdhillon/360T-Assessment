package com.example.player;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
/**
 * The MainPlayer1 class initializes Player2 (Initiator) and runs it in a separate process.
 */
public class MainPlayer2 {
    public static void main(String[] args) throws IOException {
        int messageLimit = 10;

        int player1Port = 5002;
        int player2Port = 5001;

        System.out.println("Player1 will use port: " + player1Port);
        System.out.println("Player2 will use port: " + player2Port);

        CountDownLatch serverReadyLatch = new CountDownLatch(1);
        try {
            new Player("Initiator", player1Port, player2Port, messageLimit, serverReadyLatch).start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}