package com.example.player;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;

/**
 * The Player class represents a player in the communication game.
 * Each player has a client and server part to send and receive messages.
 */
public class Player {
    private final String name;
    private final int serverPort;
    private final int clientPort;
    private final int messageLimit;
    private final CyclicBarrier barrier;

    public Player(String name, int serverPort, int clientPort, int messageLimit, CyclicBarrier barrier) {
        this.name = name;
        this.serverPort = serverPort;
        this.clientPort = clientPort;
        this.messageLimit = messageLimit;
        this.barrier = barrier;
    }

    public void start() throws IOException, InterruptedException, ExecutionException, BrokenBarrierException {
        PlayerServer server = new PlayerServer(name, messageLimit, barrier);
        Thread serverThread = new Thread(() -> {
            try {
                server.start(serverPort);
            } catch (IOException | BrokenBarrierException | InterruptedException e) {
               System.out.println(e.getMessage());
            }
        });
        serverThread.start();

        barrier.await();

        PlayerClient client = new PlayerClient();
        client.startConnection("127.0.0.1", clientPort);
        String pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        System.out.println(name + "'s PID: " + pid);

        for (int i = 0; i < messageLimit; i++) {
            String message = "Hello " + i + " from " + name + "'s client!";
            String response = client.sendMessage(message);
            System.out.println(name + " is sending: '" + message + "'.\nGot response: " + response + ".\n"+ name + " has sent " + client.getMessagesSent() + " messages.\n" );
        }

        barrier.await();

        if (server.getMessagesReceived() == client.getMessagesSent() && client.getMessagesSent() == messageLimit) {
            System.out.println(name + " has received and sent " + server.getMessagesReceived() + " messages.");
            client.stopConnection();
            server.stop();
        }
    }
}