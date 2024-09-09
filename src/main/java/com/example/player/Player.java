package com.example.player;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.concurrent.CountDownLatch;

/**
 * The Player class represents a player in the message exchange game, acting as both a client and a server.
 * Responsibilities:
 * - Initializes a PlayerServer and PlayerClient to handle message sending and receiving.
 * - Ensures proper synchronization between server and client startup.
 * - Handles the communication loop, sending messages and receiving responses.
 * - Gracefully stops the server and client after communication is complete.
 * - Ensures the correct number of messages is exchanged before termination.
 */
public class Player {
    private final String name;
    private final int serverPort;
    private final int clientPort;
    private final int messageLimit;

    public Player(String name, int serverPort, int clientPort, int messageLimit) {
        this.name = name;
        this.serverPort = serverPort;
        this.clientPort = clientPort;
        this.messageLimit = messageLimit;
    }

    public void start() throws IOException, InterruptedException {
        CountDownLatch serverReadyLatch = new CountDownLatch(1);

        PlayerServer server = new PlayerServer(name, messageLimit, serverReadyLatch);
        Thread serverThread = new Thread(() -> {
            try {
                server.start(serverPort);
            } catch (IOException e) {
                System.out.println("Server error: " + e.getMessage());
            }
        });
        serverThread.start();

        PlayerClient client = new PlayerClient();

        try {
            serverReadyLatch.await();
            client.startConnection("127.0.0.1", clientPort);

            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            System.out.println(name + "'s PID: " + pid);

            for (int i = 0; i < messageLimit; i++) {
                String message = "Hello " + i + " from " + name + "'s client!";
                String response = client.sendMessage(message);
                System.out.println(name + " sent: '" + message + "'.\nResponse: '" + response + "'");
            }

        } catch (IOException e) {
            System.out.println("Error during communication: " + e.getMessage());
        } finally {
            try {
                client.stopConnection();
            } catch (IOException e) {
                System.out.println("Error stopping client: " + e.getMessage());
            }

            try {
                serverThread.join();
                server.stop();
            } catch (IOException | InterruptedException e) {
                System.out.println("Error stopping server: " + e.getMessage());
            }
        }

        if (server.getMessagesReceived() == client.getMessagesSent() && server.getMessagesReceived() == messageLimit) {
            System.out.println(name + " has received and sent " + messageLimit + " messages.");
        } else {
            System.out.println("Error: Expected " + messageLimit + " messages, but received " + server.getMessagesReceived() + " and sent " + client.getMessagesSent() + ".");
            throw new IllegalStateException("Message exchange did not meet the expected message limit.");
        }

    }
}
