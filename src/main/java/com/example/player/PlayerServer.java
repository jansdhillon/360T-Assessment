package com.example.player;

import java.io.*;
import java.net.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * The PlayerServer class represents the server side of the player.
 * It listens for connections and processes messages from clients.
 */
public class PlayerServer {
    private String name;
    private final int messageLimit;
    private int messagesReceived = 0;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final CyclicBarrier barrier;

    public PlayerServer(String name, int messageLimit, CyclicBarrier barrier) {
        this.name = name;
        this.messageLimit = messageLimit;
        this.barrier = barrier;
    }

    public synchronized void incrementMessagesReceived() {
        this.messagesReceived++;
    }

    public synchronized int getMessagesReceived() {
        return this.messagesReceived;
    }

    public void start(int port) throws IOException, BrokenBarrierException, InterruptedException {
        serverSocket = new ServerSocket(port);
        System.out.println(name + "'s server started on port " + port);

        barrier.await();

        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        while (messagesReceived < messageLimit) {
            String receivedMessage = in.readLine();
            incrementMessagesReceived();
            String response = "Hello from " + this.name + "'s server! Received: " + receivedMessage + ". " + name + " has received " + messagesReceived + " messages.";
            out.println(response);
        }
    }

    public void stop() throws IOException {
        // Close resources only if they were initialized
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
        if (clientSocket != null) {
            clientSocket.close();
        }
        if (serverSocket != null) {
            serverSocket.close();
        }
    }
}