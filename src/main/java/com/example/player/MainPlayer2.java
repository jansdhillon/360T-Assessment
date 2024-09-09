package com.example.player;

import java.io.IOException;

/**
 * The MainPlayer2 class initializes Player2 in a separate process.
 * Responsibilities:
 * - Starts Player2 with predefined or command-line ports.
 * - Manages message exchange between Player2 and Initiator.
 * - Ensures Player2 terminates gracefully after completing communication.
 */
public class MainPlayer2 {
    public static void main(String[] args) {
        int serverPort;
        int clientPort;

        if (args.length < 2) {
            serverPort = 6666;
            clientPort = 6667;
        } else {
            serverPort = Integer.parseInt(args[0]);
            clientPort = Integer.parseInt(args[1]);
        }

        int messageLimit = 10;
        String name = "Player2";

        try {
            new Player(name, serverPort, clientPort, messageLimit).start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}
