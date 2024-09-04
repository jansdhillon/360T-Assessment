package com.example.player;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;

/**
 * The MainPlayer1 class initializes Player1 and runs it in a separate process.
 */
public class MainPlayer1 {
    public static void main(String[] args) {
        int messageLimit = 10;

        CyclicBarrier barrier = new CyclicBarrier(2);

        try {
            new Player("Player1", 6666, 6667, messageLimit, barrier).start();
        } catch (IOException | InterruptedException | ExecutionException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }
}