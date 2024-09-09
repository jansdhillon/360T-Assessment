package com.example.player;

import java.io.IOException;

/**
 * The MainPlayer1 class initializes the Initiator player in a separate process.
 * Responsibilities:
 * - Starts the Initiator player with predefined or command-line ports.
 * - Manages message exchange between Initiator and Player2.
 * - Ensures the player terminates gracefully after completing communication.
 */
public class MainPlayer1 {

    public static void main(String[] args) {
        int serverPort;
        int clientPort;

        if (args.length < 2) {
            serverPort = 6667;
            clientPort = 6666;
        } else {
            serverPort = Integer.parseInt(args[0]);
            clientPort = Integer.parseInt(args[1]);
        }

        int messageLimit = 10;
        String name = "Initiator";

        try {
            new Player(name, serverPort, clientPort, messageLimit).start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}
