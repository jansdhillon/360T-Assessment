package com.example.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player player1;
    private Player player2;
    private CyclicBarrier barrier;

    @BeforeEach
    void setUp() {
        barrier = new CyclicBarrier(2);
        player1 = new Player("Player1", 6666, 6667, 10, barrier);
        player2 = new Player("Player2", 6667, 6666, 10, barrier);
    }

    @Test
    void testPlayerInitialization() {
        assertNotNull(player1);
        assertNotNull(player2);
    }

    @Test
    void testPlayerCommunication() {
        assertDoesNotThrow(() -> {
            Thread t1 = new Thread(() -> {
                try {
                    player1.start();
                } catch (IOException | InterruptedException | ExecutionException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            });

            Thread t2 = new Thread(() -> {
                try {
                    player2.start();
                } catch (IOException | InterruptedException | ExecutionException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            });

            t1.start();
            t2.start();
            t1.join();
            t2.join();

        });
    }
}