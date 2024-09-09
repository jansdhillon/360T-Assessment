package com.example.player;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.concurrent.CountDownLatch;

/**
 * The Player class represents a player in the message exchange game, acting as both a client and a server.
 * Responsibilities:
 * - Represents a player in the message exchange game.
 * - Acts as both a client and a server by initializing PlayerServer and PlayerClient.
 * - Synchronizes the client and server startup.
 * - Sends and receives messages in a communication loop.
 * - Gracefully stops the server and client after the message exchange is complete.
 * - Verifies that the correct number of messages has been exchanged before terminating.
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

            for (int i = 1; i < messageLimit + 1; i++) {
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
           if ("Initiator".equals(name)) {
               System.out.printf("\n%s has received and sent %d messages!\n", name, messageLimit);
           }
        } else {
            System.out.println("Error: Expected " + messageLimit + " messages, but received " + server.getMessagesReceived() + " and sent " + client.getMessagesSent() + ".");
            throw new IllegalStateException("Message exchange did not meet the expected message limit.");
        }

    }
}
