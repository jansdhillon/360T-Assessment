package com.example.player;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The PlayerClient class manages the client-side communication for a player.
 * Responsibilities:
 * - Manages the client-side communication with another player's server.
 * - Establishes a connection to the server with a retry mechanism if needed.
 * - Sends messages to the server and receives responses.
 * - Tracks the number of messages sent using an atomic counter.
 * - Closes the connection and streams gracefully after communication is complete.
 */
public class PlayerClient {
    private final AtomicInteger messagesSent = new AtomicInteger(0);
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public synchronized void incrementMessagesSent() {
        this.messagesSent.incrementAndGet();
    }

    public synchronized int getMessagesSent() {
        return this.messagesSent.get();
    }

    public void startConnection(String ip, int port) throws IOException {
        int maxRetries = 10;
        int retryDelay = 1000;
        boolean connected = false;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                System.out.println("Attempting to connect to server (attempt " + attempt + ")...");
                clientSocket = new Socket();
                clientSocket.connect(new InetSocketAddress(ip, port), 5000);
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                connected = true;
                break;
            } catch (IOException e) {
                System.out.println("Failed to connect: " + e.getMessage());
                if (attempt < maxRetries) {
                    System.out.println("Retrying in " + retryDelay / 1000 + " second(s)...");
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Retry interrupted", ie);
                    }
                } else {
                    throw new IOException("Failed to establish connection after " + maxRetries + " attempts.", e);
                }
            }
        }

        if (!connected) {
            throw new IOException("Could not connect to the server.");
        }
    }

    public String sendMessage(String msg) throws IOException {
        if (out == null) {
            throw new IOException("Client connection not initialized. Cannot send message.");
        }

        out.println(msg);
        incrementMessagesSent();

        String response = in.readLine();
        if (response == null) {
            throw new IOException("No response from server.");
        }
        return response;
    }

    public void stopConnection() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (clientSocket != null) clientSocket.close();
    }
}
