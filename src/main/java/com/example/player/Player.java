package com.example.player;

import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * The Player class represents a player in the communication game.
 * Each player has a client and server part to send and receive messages.
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
        PlayerServer server = new PlayerServer(name, messageLimit);
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
            client.startConnection("127.0.0.1", clientPort);
        } catch (IOException e) {
            e.getMessage();
        }


        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        System.out.println(name + "'s PID: " + pid);


        for (int i = 0; i < messageLimit; i++) {
            String message = "Hello " + i + " from " + name + "'s client!";
            String response = client.sendMessage(message);
            System.out.println(name + " sent: '" + message + "'.\nResponse: " + "'" + response + "'");
        }

        client.stopConnection();
        serverThread.join();
        server.stop();

        if (server.getMessagesReceived() == client.getMessagesSent() && server.getMessagesReceived() == messageLimit) {
            System.out.println(name + " has received and sent " + messageLimit + " messages.");
        } else {
            System.out.println("Fail");
        }


    }
}