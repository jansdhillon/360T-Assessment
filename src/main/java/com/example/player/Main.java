package com.example.player;

import java.util.concurrent.CyclicBarrier;

/**
 * The Main class initializes two players and runs them in the same process.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        int messageLimit = 10;

        CyclicBarrier barrier = new CyclicBarrier(2);

        Thread t1 = new Thread(() -> {
            try {
                new Player("Player1", 6666, 6667, messageLimit, barrier).start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                new Player("initiator", 6667, 6666, messageLimit, barrier).start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

    }
}