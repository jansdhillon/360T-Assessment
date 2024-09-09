package com.example.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player player1;
    private Player player2;


    @BeforeEach
    void setUp() {

        player1 = new Player("Initiator", 6666, 6667, 10);
        player2 = new Player("Player2", 6667, 6666, 10);
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
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            Thread t2 = new Thread(() -> {
                try {
                    player2.start();
                } catch (Exception e) {
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