package com.example.player;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;

/**
 * The MainPlayer2 class initializes Initiator and runs it in a separate process.
 */
public class MainPlayer2 {
    public static void main(String[] args) {
        int messageLimit = 10;

        CyclicBarrier barrier = new CyclicBarrier(2);

        try {
            new Player("Initiator", 6667, 6666, messageLimit, barrier).start();
        } catch (IOException | InterruptedException | ExecutionException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }
}