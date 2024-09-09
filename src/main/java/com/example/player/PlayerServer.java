package com.example.player;

import java.io.*;
import java.net.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The PlayerServer class manages the server-side communication for a player.
 * Responsibilities:
 * - Listens for connections from the client.
 * - Receives messages and sends responses with a message counter.
 * - Tracks the number of messages received.
 * - Signals readiness to accept connections and ensures synchronization.
 * - Gracefully shuts down the server and closes connections after communication is complete.
 */
public class PlayerServer {
    private String name;
    private final int messageLimit;
    private final AtomicInteger messagesReceived = new AtomicInteger(0);
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final CountDownLatch serverReadyLatch;

    public PlayerServer(String name, int messageLimit, CountDownLatch serverReadyLatch) {
        this.name = name;
        this.messageLimit = messageLimit;
        this.serverReadyLatch = serverReadyLatch;
    }

    public void start(int port) throws IOException {
        System.out.println(name + "'s server is starting on port " + port);


        try {
            // Wait for a second for the server to start before client attempts to connect
            Thread.sleep(1000);
            serverSocket = new ServerSocket(port);
            serverReadyLatch.countDown();
            clientSocket = serverSocket.accept();
            System.out.println("A client connected to " + name + "'s server");
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while (messagesReceived.get() < messageLimit) {
                try {
                    String receivedMessage = in.readLine();
                    if (receivedMessage == null) break;
                    incrementMessagesReceived();
                    String response = receivedMessage + " (message #" + messagesReceived.get() + ")";
                    out.println(response);
                } catch (IOException e) {
                    System.out.println("Stream closed or error occurred: " + e.getMessage());
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error during communication: " + e.getMessage());
        } finally {
            stop();
        }
    }

    public void stop() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (clientSocket != null) clientSocket.close();
        if (serverSocket != null) serverSocket.close();
    }

    public void incrementMessagesReceived() {
        messagesReceived.incrementAndGet();
    }

    public int getMessagesReceived() {
        return messagesReceived.get();
    }
}
